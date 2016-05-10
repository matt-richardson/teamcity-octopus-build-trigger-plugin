package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.ServiceLocator;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.PolledTriggerContext;
import jetbrains.buildServer.buildTriggers.async.*;
import jetbrains.buildServer.buildTriggers.async.impl.AsyncPolledBuildTrigger;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.impl.LogUtil;
import jetbrains.buildServer.users.SUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class CustomAsyncPolledBuildTrigger<TItem> extends AsyncPolledBuildTrigger<TItem> {
    private final ExecutorService executorService;
    final Logger log;
    private final CheckJobStatusStorage<TItem> storage;
    final CustomAsyncBuildTrigger<TItem> asyncBuildTrigger;
    final ServiceLocator serviceLocator;

    public CustomAsyncPolledBuildTrigger(ExecutorService executorService,
                                         Logger log,
                                         CheckJobStatusStorage<TItem> storage,
                                         int pollInterval,
                                         CustomAsyncBuildTrigger<TItem> asyncBuildTrigger,
                                         ServiceLocator serviceLocator) {
        super(executorService, log, storage, pollInterval, asyncBuildTrigger);
        this.executorService = executorService;
        this.log = log;
        this.storage = storage;
        this.asyncBuildTrigger = asyncBuildTrigger;
        this.serviceLocator = serviceLocator;
    }

    @Override
    public void triggerBuild(@NotNull PolledTriggerContext context) throws BuildTriggerException {
        CustomCheckResult<TItem> result = (CustomCheckResult<TItem>)checkJobStatus(context.getBuildType(), context.getTriggerDescriptor(), context.getCustomDataStorage());
        if (result != null) {
            UUID correlationId = result.getCorrelationId();
            JobResult jobResult = mapCheckResultToJobResult(result, correlationId);

            if (jobResult.shouldTriggerBuild()) {
                SBuildType buildType = serviceLocator.getSingletonService(ProjectManager.class)
                        .findBuildTypeByExternalId(context.getBuildType().getExternalId());
                SUser user = null;
                final BuildCustomizer customizer = serviceLocator.getSingletonService(BuildCustomizerFactory.class)
                        .createBuildCustomizer(buildType, user);
                customizer.setParameters(jobResult.properties);
                try {
                    log.debug(String.format("%s: Creating build promotion", correlationId));
                    BuildPromotionEx promotion = (BuildPromotionEx)customizer.createPromotion();
                    log.debug(String.format("%s: Build promotion %d created", correlationId, promotion.getId()));

                    log.debug(String.format("%s: Adding '%s' to queue", correlationId, buildType.toString()));
                    SQueuedBuild addResult = ((BuildTypeEx)buildType).addToQueue(promotion, jobResult.triggeredBy);
                    log.info(String.format("%s: Added '%s' to queue", correlationId, addResult.toString()));
                }
                catch(Exception e) {
                    log.error(String.format("%s: Failed to create the build promotion", correlationId), e);
                }
            }

            if(jobResult.ex != null) {
                throw jobResult.ex;
            }
        }
    }

    @NotNull
    private JobResult mapCheckResultToJobResult(@NotNull CheckResult<TItem> checkResult, UUID correlationId) {
        JobResult summary = new JobResult();
        Throwable generalError = checkResult.getGeneralError();
        if(generalError != null) {
            summary.ex = asyncBuildTrigger.makeTriggerException(generalError);
        } else {
            Collection<DetectionException> errors = checkResult.getCheckErrors().values();
            if(!errors.isEmpty()) {
                DetectionException cause = errors.iterator().next();
                summary.ex = asyncBuildTrigger.makeTriggerException(cause);
            }
        }

        Collection<TItem> updated = checkResult.getUpdated();
        if (!updated.isEmpty()) {
            log.debug(String.format("%s: changes detected in %s", correlationId, updated));
            TItem update = updated.iterator().next();
            summary.triggeredBy = asyncBuildTrigger.getRequestorString(update);
            Map<String, String> properties = asyncBuildTrigger.getProperties(update);
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                log.debug(String.format("%s: property['%s'] = '%s'", correlationId, entry.getKey(), entry.getValue()));
            }
            summary.properties = properties;
        } else {
            log.debug(String.format("%s: changes not detected", correlationId));
        }

        return summary;
    }

    @Nullable
    @Override
    public CheckResult<TItem> checkJobStatus(@NotNull SBuildType buildType, @NotNull BuildTriggerDescriptor triggerDescriptor, @NotNull CustomDataStorage storage) throws BuildTriggerException {
        CheckResult<TItem> result = null;

        try {
            CheckJobStatus<TItem> checkJobStatus = this.storage.getJobStatus(buildType, triggerDescriptor);
            if(checkJobStatus != null && checkJobStatus.isIdle()) {
                CheckResult<TItem> parameters = checkJobStatus.getCheckResult();
                if(parameters != null) {
                    result = parameters;
                }
            }

            boolean isToStartNewCheck = true;
            if (checkJobStatus != null) {
                int pollInterval = asyncBuildTrigger.getPollIntervalInMilliseconds();
                boolean pollIntervalIsSatisfied = System.currentTimeMillis() - checkJobStatus.getLastResultTimeMillis() > (long) (pollInterval * 1000);
                isToStartNewCheck = checkJobStatus.isIdle() && pollIntervalIsSatisfied;
                checkJobStatus.resetResult();
            }

            if(isToStartNewCheck) {
                UUID correlationId = UUID.randomUUID();
                CustomCheckJob<TItem> job = this.asyncBuildTrigger.createJob(buildType.toString(), storage, triggerDescriptor.getProperties(), correlationId);
                if(!job.allowSchedule(triggerDescriptor)) {
                    this.submitCheckJob(this.storage.getOrCreateJobStatus(buildType, triggerDescriptor), job, correlationId);
                }
            }
        } catch (CheckJobCreationException ex) {
            log.warn(String.format("%s; Build type: %s", ex.getMessage(), LogUtil.describe(buildType)));
            log.debug(ex);
            if(ex.isReportable()) {
                throw new BuildTriggerException(ex.getMessage(), ex);
            }
        }

        return result;
    }

    private void submitCheckJob(@NotNull final CheckJobStatus<TItem> checkJobStatus, @NotNull final CustomCheckJob<TItem> job, UUID correlationId) {
        checkJobStatus.setIdle(false);

        try {
            this.executorService.execute(() -> {
                try {
                    CheckResult<TItem> checkResult = job.perform(correlationId);
                    checkJobStatus.setCheckResult(checkResult);
                } catch (Throwable var5) {
                    checkJobStatus.setCheckResult(asyncBuildTrigger.createCrashOnSubmitResult(var5, correlationId));
                } finally {
                    checkJobStatus.setIdle(true);
                }

            });
        } catch (Throwable var4) {
            checkJobStatus.setIdle(true);
            log.error("Couldn\'t start check job", var4);
        }

    }

    private class JobResult {
        @Nullable
        private String triggeredBy;
        @Nullable
        private BuildTriggerException ex;
        @NotNull
        private Map<String, String> properties;

        public boolean shouldTriggerBuild() {
            return triggeredBy != null;
        }
    }
}
