package com.mjrichardson.teamCity.buildTriggers;

import com.brsanthu.googleanalytics.EventHit;
import com.brsanthu.googleanalytics.ExceptionHit;
import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.GoogleAnalyticsConfig;
import com.codahale.metrics.MetricRegistry;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codahale.metrics.MetricRegistry.name;

public class AnalyticsTrackerImpl implements AnalyticsTracker {
    @NotNull
    private static final Logger LOG = Logger.getInstance(AnalyticsTrackerImpl.class.getName());
    @NotNull
    private final MetricRegistry metricRegistry;

    private final String pluginVersion;
    private final String teamCityVersion;
    private final String applicationName = "teamcity-octopus-build-trigger-plugin";
    private static final Pattern ipAddressPattern = Pattern.compile("\\d*\\.\\d*\\.\\d*\\.\\d*");
    private static final Pattern urlPattern = Pattern.compile("(http|https)://(.*?)(/|:)");
    private final BuildTriggerProperties buildTriggerProperties;
    private GoogleAnalytics ga = null;
    private String octopusVersion = "not-set";
    private String octopusApiVersion = "not-set";

    public AnalyticsTrackerImpl(@NotNull final PluginDescriptor pluginDescriptor, SBuildServer buildServer, @NotNull MetricRegistry metricRegistry, BuildTriggerProperties buildTriggerProperties) {
        this.metricRegistry = metricRegistry;
        this.buildTriggerProperties = buildTriggerProperties;
        this.pluginVersion = pluginDescriptor.getPluginVersion();
        this.teamCityVersion = buildServer.getFullServerVersion();
        String trackingId = pluginDescriptor.getParameterValue("AnalyticsTrackingId");

        boolean enabled = this.buildTriggerProperties.isAnalyticsEnabled();
        LOG.info(String.format("AnalyticsTrackerImpl instantiated for plugin version %s in teamcity version %s. Tracking enabled: %s.",
                pluginVersion, teamCityVersion, enabled));
        try {
            GoogleAnalyticsConfig config = new GoogleAnalyticsConfig()
                    .setEnabled(enabled);
            ga = new GoogleAnalytics(config, trackingId);
        }
        catch (Throwable e) {
            LOG.warn("Analytics initialisation failed. Disabling analytics", e);
        }
    }

    public void postEvent(EventCategory eventCategory, EventAction eventAction, UUID correlationId){

        metricRegistry.meter(name(AnalyticsTrackerImpl.class, eventCategory.name(), eventAction.name())).mark();

        if (ga == null)
            return;

        checkEnabledState(correlationId);

        if (!ga.getConfig().isEnabled())
            return;

        LOG.info(String.format("%s: Posting analytics event - %s: %s", correlationId, eventCategory.name(), eventAction.name()));

        try {
            EventHit request = new EventHit(eventCategory.name(), eventAction.name())
                    .applicationName(applicationName)
                    .applicationVersion(pluginVersion)
                    .customDimension(0, teamCityVersion)
                    .customDimension(1, octopusVersion)
                    .customDimension(2, octopusApiVersion);
            ga.postAsync(request);
        }
        catch (Throwable e) {
            LOG.warn(String.format("%s: Analytics postEvent failed", correlationId), e);
        }
    }

    public void postException(Exception e, UUID correlationId) {

        metricRegistry.meter(name(AnalyticsTrackerImpl.class, "exception", e.getClass().getName())).mark();

        if (ga == null)
            return;

        checkEnabledState(correlationId);

        if (!ga.getConfig().isEnabled())
            return;

        LOG.info(String.format("%s: Posting analytics exception - %s", correlationId, e.getMessage()));

        try {
            String exceptionDetail = maskException(e);
            ExceptionHit request = new ExceptionHit(exceptionDetail)
                    .applicationName(applicationName)
                    .applicationVersion(pluginVersion)
                    .customDimension(0, teamCityVersion)
                    .customDimension(1, octopusVersion)
                    .customDimension(2, octopusApiVersion);
            ga.postAsync(request);
        }
        catch (Throwable ex) {
            LOG.warn(String.format("%s: Analytics postException failed", correlationId), ex);
        }
    }

    private synchronized void checkEnabledState(UUID correlationId) {
        boolean newState = buildTriggerProperties.isAnalyticsEnabled();
        boolean oldState = ga.getConfig().isEnabled();
        if (newState != oldState) {
            LOG.info(String.format("%s: Changing analytics enabled state to %s.", correlationId, newState));
            ga.getConfig().setEnabled(newState);
        }
    }

    @Override
    public void setOctopusVersion(String octopusVersion) {
        this.octopusVersion = octopusVersion;
    }

    @Override
    public void setOctopusApiVersion(String octopusApiVersion) {
        this.octopusApiVersion = octopusApiVersion;
    }

    static String maskException(Exception e) {
        String result = e.toString();
        Matcher ipAddressMatcher = ipAddressPattern.matcher(result);
        String ipAddress = null;
        if (ipAddressMatcher.find())
            ipAddress = ipAddressMatcher.group(0);

        Matcher urlMatcher = urlPattern.matcher(result);
        String url = null;
        if (urlMatcher.find())
            url = urlMatcher.group(2);

        Pattern urlPatternWithPort = Pattern.compile("(" + url + ":\\d+)");

        Matcher urlPatternMatcher = urlPatternWithPort.matcher(result);
        String urlWithPort = null;
        if (urlPatternMatcher.find())
            urlWithPort = urlPatternMatcher.group(1);

        if (ipAddress != null)
            result = result.replace(ipAddress, "*****");
        if (urlWithPort != null)
            result = result.replace(urlWithPort, "*****:*****");
        if (url != null)
            result = result.replace(url, "*****");

        return result;
    }
}
