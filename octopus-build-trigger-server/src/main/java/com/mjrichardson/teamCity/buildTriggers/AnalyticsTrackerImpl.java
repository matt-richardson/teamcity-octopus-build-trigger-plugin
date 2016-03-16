package com.mjrichardson.teamCity.buildTriggers;

import com.brsanthu.googleanalytics.EventHit;
import com.brsanthu.googleanalytics.ExceptionHit;
import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.GoogleAnalyticsConfig;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyticsTrackerImpl implements AnalyticsTracker {
    private final String trackingId = "UA-75050025-1";
    @NotNull
    private static final Logger LOG = Logger.getInstance(AnalyticsTrackerImpl.class.getName());
    private final String pluginVersion;
    private final String teamCityVersion;
    private final String applicationName = "teamcity-octopus-build-trigger-plugin";
    private static final Pattern ipAddressPattern = Pattern.compile("\\d*\\.\\d*\\.\\d*\\.\\d*");
    private static final Pattern urlPattern = Pattern.compile("(http|https)://(.*?)/");
    private GoogleAnalytics ga = null;

    public AnalyticsTrackerImpl(@NotNull final PluginDescriptor pluginDescriptor, SBuildServer buildServer) {
        this.pluginVersion = pluginDescriptor.getPluginVersion();
        this.teamCityVersion = buildServer.getFullServerVersion();

        LOG.info(String.format("AnalyticsTrackerImpl instantiated for plugin version %s in teamcity version %s",
                pluginVersion, teamCityVersion));
        try {
            GoogleAnalyticsConfig config = new GoogleAnalyticsConfig()
                    .setEnabled(OctopusBuildTriggerUtil.getAnalyticsEnabled());
            ga = new GoogleAnalytics(config, trackingId);
        }
        catch (Throwable e) {
            LOG.warn("Analytics initialisation failed. Disabling analytics", e);
        }
    }

    public void postEvent(EventCategory eventCategory, EventAction eventAction){
        LOG.info(String.format("Posting analytics event - %s: %s", eventCategory.name(), eventAction.name()));
        if (ga == null)
            return;
        try {
            EventHit request = new EventHit(eventCategory.name(), eventAction.name())
                    .applicationName(applicationName)
                    .applicationVersion(pluginVersion)
                    .customDimension(0, teamCityVersion);
            ga.postAsync(request);
        }
        catch (Throwable e) {
            LOG.warn("Analytics postEvent failed", e);
        }
    }

    public void postException(Exception e) {
        LOG.info(String.format("Posting analytics exception - %s", e.getMessage()));
        if (ga == null)
            return;
        try {
            String exceptionDetail = maskException(e);
            ExceptionHit request = new ExceptionHit(exceptionDetail)
                    .applicationName(applicationName)
                    .applicationVersion(pluginVersion)
                    .customDimension(0, teamCityVersion);
            ga.postAsync(request);
        }
        catch (Throwable ex) {
            LOG.warn("Analytics postException failed", ex);
        }
    }

    static String maskException(Exception e) {
        String result = e.toString();
        Matcher ipAddressMatcher = ipAddressPattern.matcher(result);
        String ipAddress = "";
        if (ipAddressMatcher.find())
            ipAddress = ipAddressMatcher.group(0);

        Matcher urlMatcher = urlPattern.matcher(result);
        String url = "";
        if (urlMatcher.find())
            url = urlMatcher.group(2);

        Pattern urlPatternWithPort = Pattern.compile("(" + url + ":\\d*) ");

        Matcher urlPatternMatcher = urlPatternWithPort.matcher(result);
        String urlWithPort = "";
        if (urlPatternMatcher.find())
            urlWithPort = urlPatternMatcher.group(1);

        return result
                .replace(ipAddress, "*****")
                .replace(urlWithPort, "*****:*****")
                .replace(url, "*****")
        ;
    }
}
