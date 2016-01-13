/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package matt_richardson.teamCity.buildTriggers.octopusDeploy;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.buildTriggers.*;
import jetbrains.buildServer.buildTriggers.async.*;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static matt_richardson.teamCity.buildTriggers.octopusDeploy.OctopusBuildTriggerUtil.*;

/**
 * User: vbedrosova
 * Date: 06.12.10
 * Time: 13:19
 */
public final class OctopusBuildTrigger extends BuildTriggerService {
  @NotNull
  private static final Logger LOG = Logger.getInstance(Loggers.VCS_CATEGORY + OctopusBuildTrigger.class);

  private static final String DISPLAY_NAME = "Octopus build trigger";

  @NotNull
  private final PluginDescriptor myPluginDescriptor;
  @NotNull
  private final BuildTriggeringPolicy myPolicy;

  public OctopusBuildTrigger(@NotNull final PluginDescriptor pluginDescriptor,
                         @NotNull final AsyncBuildTriggerFactory triggerFactory) {
    myPluginDescriptor = pluginDescriptor;
    myPolicy = triggerFactory.createBuildTrigger(Spec.class, getAsyncBuildTrigger(), LOG, getPollInterval());
  }

  @NotNull
  @Override
  public String getName() {
    return "octopusBuildTrigger";
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return DISPLAY_NAME;
  }

  @NotNull
  @Override
  public String describeTrigger(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
    return "Wait for a new deployment at " + buildTriggerDescriptor.getProperties().get(OCTOPUS_URL) + " URL";
  }

  @NotNull
  @Override
  public BuildTriggeringPolicy getBuildTriggeringPolicy() {
    return myPolicy;
  }

  private int getPollInterval() {
    return TeamCityProperties.getInteger(POLL_INTERVAL_PROP, DEFAULT_POLL_INTERVAL);
  }

  @Override
  public PropertiesProcessor getTriggerPropertiesProcessor() {
    return new PropertiesProcessor() {
      public Collection<InvalidProperty> process(Map<String, String> properties) {
        final ArrayList<InvalidProperty> invalidProps = new ArrayList<InvalidProperty>();
        final String url = properties.get(OCTOPUS_URL);
        if (StringUtil.isEmptyOrSpaces(url)) {
          invalidProps.add(new InvalidProperty(OCTOPUS_URL, "URL must be specified"));
        }
        final String apiKey = properties.get(OCTOPUS_APIKEY);
        if (StringUtil.isEmptyOrSpaces(url)) {
          invalidProps.add(new InvalidProperty(OCTOPUS_APIKEY, "API Key must be specified"));
        }
        final String err = (new OctopusDeploymentsProvider()).checkOctopusConnectivity(url, apiKey);
        if (StringUtil.isNotEmpty(err)) {
          invalidProps.add(new InvalidProperty(OCTOPUS_URL, err));
        }
        return invalidProps;
      }
    };
  }

  @Override
  public String getEditParametersUrl() {
    return myPluginDescriptor.getPluginResourcesPath("editOctopusBuildTrigger.jsp");
  }

  @Override
  public boolean isMultipleTriggersPerBuildTypeAllowed() {
    return true;
  }

  @NotNull
  private AsyncBuildTrigger<Spec> getAsyncBuildTrigger() {
    return new AsyncBuildTrigger<Spec>() {
      @NotNull
      public BuildTriggerException makeTriggerException(@NotNull Throwable throwable) {
        throw new BuildTriggerException(getDisplayName() + " failed with error: " + throwable.getMessage(), throwable);
      }

      @NotNull
      public String getRequestorString(@NotNull Spec spec) {
        return getDisplayName() + " " + spec.getUrl();
      }

      public int getPollInterval(@NotNull AsyncTriggerParameters parameters) {
        return OctopusBuildTrigger.this.getPollInterval();
      }

      @NotNull
      public CheckJob<Spec> createJob(@NotNull final AsyncTriggerParameters asyncTriggerParameters) throws CheckJobCreationException {
        return new CheckJob<Spec>() {
          @NotNull
          public CheckResult<Spec> perform() {
            final Map<String, String> props = asyncTriggerParameters.getTriggerDescriptor().getProperties();

            final String octopusUrl = props.get(OCTOPUS_URL);
            final String octopusApiKey = props.get(OCTOPUS_APIKEY);
            final String octopusProject = props.get(OCTOPUS_PROJECT);

            if (StringUtil.isEmptyOrSpaces(octopusUrl)) {
              return createErrorResult(getDisplayName() + " settings are invalid (empty url) in build configuration " + asyncTriggerParameters.getBuildType());
            }
            if (StringUtil.isEmptyOrSpaces(octopusApiKey)) {
              return createErrorResult(getDisplayName() + " settings are invalid (empty api key) in build configuration " + asyncTriggerParameters.getBuildType());
            }
            if (StringUtil.isEmptyOrSpaces(octopusProject)) {
              return createErrorResult(getDisplayName() + " settings are invalid (empty project) in build configuration " + asyncTriggerParameters.getBuildType());
            }

            LOG.debug(getDisplayName() + " checks for new deployments for project " + octopusProject + " on server " + octopusUrl);

            final String dataStorageKey = (octopusUrl + "|" + octopusProject).toLowerCase();
            final Spec spec = new Spec(octopusUrl);

            try {
              final String oldStoredData = asyncTriggerParameters.getCustomDataStorage().getValue(dataStorageKey);
              final Deployments oldDeployments = new Deployments(oldStoredData);
              final Deployments newDeployments = new OctopusDeploymentsProvider().getDeployments(octopusUrl, octopusApiKey, octopusProject, oldDeployments);

              //todo: fix so that only store that one OD deployment has happened here, not multiple. We could inadvertendly miss deployments
              final String newStoredData = newDeployments.toString();

              if (!newDeployments.equals(oldDeployments)) {
                asyncTriggerParameters.getCustomDataStorage().putValue(dataStorageKey, newStoredData);

                if (oldDeployments.isEmpty()) { // do not trigger build after adding trigger (oldHash == null)
                  LOG.debug(getDisplayName() + " no previous data for server " + octopusUrl + ", project " + octopusProject + ": null" + " -> " + newStoredData);
                  return createEmptyResult();
                }

                LOG.info(getDisplayName() + " new deployments on " + octopusUrl + " for project " + octopusProject + ": " + oldStoredData + " -> " + newStoredData);
                return createUpdatedResult(spec);
              }

              LOG.debug(getDisplayName() + " resource not changed " + octopusUrl + " for project " + octopusProject + ": " + oldStoredData + " -> " + newStoredData);
              return createEmptyResult();

            } catch (Exception e) {
              return createThrowableResult(spec, e);
            }
          }

          public boolean allowSchedule(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
            return false;
          }
        };
      }

      @NotNull
      public CheckResult<Spec> createCrashOnSubmitResult(@NotNull Throwable throwable) {
        return createThrowableResult(throwable);
      }
    };
  }

  @NotNull
  private SpecCheckResult createEmptyResult() {
    return new SpecCheckResult();
  }

  @NotNull
  private SpecCheckResult createUpdatedResult(@NotNull Spec spec) {
    return new SpecCheckResult(Collections.singleton(spec), Collections.<Spec, DetectionException>emptyMap());
  }

  @NotNull
  private SpecCheckResult createThrowableResult(@NotNull Throwable throwable) {
    return new SpecCheckResult(throwable);
  }

  @NotNull
  private SpecCheckResult createThrowableResult(@NotNull Spec spec, @NotNull Throwable throwable) {
    return new SpecCheckResult(Collections.singleton(spec), Collections.singletonMap(spec, new DetectionException(throwable.getMessage(), throwable)));
  }

  @NotNull
  private SpecCheckResult createErrorResult(@NotNull String error) {
    return new SpecCheckResult(new BuildTriggerException(error));
  }

  private static class SpecCheckResult extends CheckResult<Spec> {
    private SpecCheckResult() { super(); }
    private SpecCheckResult(@NotNull Collection<Spec> updated, @NotNull Map<Spec, DetectionException> errors) { super(updated, errors); }
    private SpecCheckResult(@NotNull Throwable generalError) { super(generalError); }
  }

  private static class Spec {
    @NotNull
    private final String myUrl;

    private Spec(@NotNull String url) {
      myUrl = url;
    }

    @NotNull
    private String getUrl() {
      return myUrl;
    }
  }
}
