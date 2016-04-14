package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.ServiceLocator;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.PolledTriggerContext;
import jetbrains.buildServer.buildTriggers.async.CheckJobStatusStorage;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.buildTriggers.async.DetectionException;
import jetbrains.buildServer.buildTriggers.async.impl.AsyncPolledBuildTrigger;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.users.SUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class CustomAsyncPolledBuildTrigger<TItem> extends AsyncPolledBuildTrigger<TItem> {
    final Logger log;
    final CustomAsyncBuildTrigger<TItem> asyncBuildTrigger;
    final ServiceLocator serviceLocator;

    public CustomAsyncPolledBuildTrigger(ExecutorService executorService,
                                         Logger log,
                                         CheckJobStatusStorage<TItem> storage,
                                         int pollInterval,
                                         CustomAsyncBuildTrigger<TItem> asyncBuildTrigger,
                                         ServiceLocator serviceLocator) {
        super(executorService, log, storage, pollInterval, asyncBuildTrigger);
        this.log = log;
        this.asyncBuildTrigger = asyncBuildTrigger;
        this.serviceLocator = serviceLocator;
    }

    @Override
    public void triggerBuild(@NotNull PolledTriggerContext context) throws BuildTriggerException {
        CheckResult result = checkJobStatus(context.getBuildType(), context.getTriggerDescriptor(), context.getCustomDataStorage());
        if (result != null) {
            JobResult jobResult = mapCheckResultToJobResult(result);

            if (jobResult.shouldTriggerBuild()) {
                SBuildType buildType = serviceLocator.getSingletonService(ProjectManager.class)
                        .findBuildTypeByExternalId(context.getBuildType().getExternalId());
                SUser user = null;
                final BuildCustomizer customizer = serviceLocator.getSingletonService(BuildCustomizerFactory.class)
                        .createBuildCustomizer(buildType, user);
                customizer.setParameters(jobResult.properties);
                try {
                    log.debug("Creating build promotion");
                    BuildPromotionEx promotion = (BuildPromotionEx)customizer.createPromotion();
                    log.debug("Build promotion " + promotion.getId() + " created");

                    log.debug("Adding '" + buildType.toString() + "' to queue");
                    SQueuedBuild addResult = ((BuildTypeEx)buildType).addToQueue(promotion, jobResult.triggeredBy);
                    log.info("Added '" + addResult.toString() + "' to queue");
                }
                catch(Exception e) {
                    log.error("Failed to create the build promotion", e);
                }
            }

            if(jobResult.ex != null) {
                throw jobResult.ex;
            }
        }
    }

    @NotNull
    private JobResult mapCheckResultToJobResult(@NotNull CheckResult<TItem> checkResult) {
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
            this.log.debug("changes detected in " + updated);
            TItem update = updated.iterator().next();
            summary.triggeredBy = asyncBuildTrigger.getRequestorString(update);
            //todo: dont add null properties
            Map<String, String> properties = asyncBuildTrigger.getProperties(update);
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                this.log.debug("property['" + entry.getKey() + "'] = '" + entry.getValue() + "'");
            }
            summary.properties = properties;
        } else {
            this.log.debug("changes not detected");
        }

        return summary;
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
