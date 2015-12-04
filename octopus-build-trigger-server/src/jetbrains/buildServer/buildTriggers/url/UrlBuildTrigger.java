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

package jetbrains.buildServer.buildTriggers.url;

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

import static jetbrains.buildServer.buildTriggers.url.UrlBuildTriggerUtil.*;

/**
 * User: vbedrosova
 * Date: 06.12.10
 * Time: 13:19
 */
public final class UrlBuildTrigger extends BuildTriggerService {
  @NotNull
  private static final Logger LOG = Logger.getInstance(Loggers.VCS_CATEGORY + UrlBuildTrigger.class);

  private static final String DISPLAY_NAME = "URL build trigger";

  @NotNull
  private final PluginDescriptor myPluginDescriptor;
  @NotNull
  private final BuildTriggeringPolicy myPolicy;

  public UrlBuildTrigger(@NotNull final PluginDescriptor pluginDescriptor,
                         @NotNull final AsyncBuildTriggerFactory triggerFactory) {
    myPluginDescriptor = pluginDescriptor;
    myPolicy = triggerFactory.createBuildTrigger(Spec.class, getAsyncBuildTrigger(), LOG, getPollInterval());
  }

  @NotNull
  @Override
  public String getName() {
    return "urlBuildTrigger";
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return DISPLAY_NAME;
  }

  @NotNull
  @Override
  public String describeTrigger(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
    return "Wait for a change at " + buildTriggerDescriptor.getProperties().get(URL_PARAM) + " URL";
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
        final String url = properties.get(URL_PARAM);
        if (StringUtil.isEmptyOrSpaces(url)) {
          invalidProps.add(new InvalidProperty(URL_PARAM, "URL must be specified"));
        }
        final String err = ResourceHashProviderFactory.checkUrl(url);
        if (StringUtil.isNotEmpty(err)) {
          invalidProps.add(new InvalidProperty(URL_PARAM, err));
        }
        return invalidProps;
      }
    };
  }

  @Override
  public String getEditParametersUrl() {
    return myPluginDescriptor.getPluginResourcesPath("editUrlBuildTrigger.jsp");
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
        return UrlBuildTrigger.this.getPollInterval();
      }

      @NotNull
      public CheckJob<Spec> createJob(@NotNull final AsyncTriggerParameters asyncTriggerParameters) throws CheckJobCreationException {
        return new CheckJob<Spec>() {
          @NotNull
          public CheckResult<Spec> perform() {
            final Map<String, String> props = asyncTriggerParameters.getTriggerDescriptor().getProperties();

            final String urlStr = props.get(URL_PARAM);

            if (StringUtil.isEmptyOrSpaces(urlStr)) {
              return createErrorResult(getDisplayName() + " settings are invalid in build configuration " + asyncTriggerParameters.getBuildType());
            }

            LOG.debug(getDisplayName() + " checks if resource changed " + urlStr);

            final Spec spec = new Spec(urlStr);

            try {
              final String oldHash = asyncTriggerParameters.getCustomDataStorage().getValue(urlStr);
              final String newHash = ResourceHashProviderFactory.createResourceHashProvider(urlStr)
                .getResourceHash(TriggerParameters.create(
                  urlStr, props.get(USERNAME_PARAM), props.get(PASSWORD_PARAM), TeamCityProperties.getInteger(CONNECTION_TIMEOUT_PROP, DEFAULT_CONNECTION_TIMEOUT), oldHash
                ));

              if (!newHash.equals(oldHash)) {
                asyncTriggerParameters.getCustomDataStorage().putValue(urlStr, newHash);

                if (oldHash == null) { // do not trigger build after adding trigger (oldHash == null)
                  LOG.debug(getDisplayName() + " no previous data for resource " + urlStr + ": null" + " -> " + newHash);
                  return createEmptyResult();
                }

                LOG.info(getDisplayName() + " resource changed " + urlStr + ": " + oldHash + " -> " + newHash);
                return createUpdatedResult(spec);
              }

              LOG.debug(getDisplayName() + " resource not changed " + urlStr + ": " + oldHash + " -> " + newHash);
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
