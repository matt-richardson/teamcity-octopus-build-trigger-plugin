package com.mjrichardson.teamCity.buildTriggers;

import com.brsanthu.googleanalytics.*;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

public class AnalyticsTrackerImpl implements AnalyticsTracker {
    private final String trackingId = "UA-75050025-1";
    @NotNull
    private static final Logger LOG = Logger.getInstance(AnalyticsTrackerImpl.class.getName());
    private final String pluginVersion;
    private final String teamCityVersion;

    public AnalyticsTrackerImpl(@NotNull final PluginDescriptor pluginDescriptor, SBuildServer buildServer) {
        this.pluginVersion = pluginDescriptor.getPluginVersion();
        this.teamCityVersion = buildServer.getFullServerVersion();

        LOG.info(String.format("AnalyticsTrackerImpl instantiated for plugin version %s in teamcity version %s",
                pluginVersion, teamCityVersion));

        //run this on a background thread, as it was getting some crazy spring initialisation errors:
        //Caused by: java.lang.LinkageError: loader constraint violation: when resolving method
        //  "org.slf4j.impl.StaticLoggerBinder.getLoggerFactory()Lorg/slf4j/ILoggerFactory;" the class loader
        //  (instance of jetbrains/buildServer/plugins/classLoaders/PluginStandaloneClassLoader) of the current class,
        //  org/slf4j/LoggerFactory, and the class loader (instance of org/apache/catalina/loader/WebappClassLoader)
        //  for the method's defining class, org/slf4j/impl/StaticLoggerBinder, have different Class objects for the
        //  type org/slf4j/ILoggerFactory used in the signature
        //at org.slf4j.LoggerFactory.getILoggerFactory(LoggerFactory.java:299)
        //at org.slf4j.LoggerFactory.getLogger(LoggerFactory.java:269)
        //at org.slf4j.LoggerFactory.getLogger(LoggerFactory.java:281)
        //at com.brsanthu.googleanalytics.GoogleAnalytics.<clinit>(GoogleAnalytics.java:70)
        //at com.mjrichardson.teamCity.buildTriggers.AnalyticsTrackerImpl.<init>(AnalyticsTrackerImpl.java:15)
        //at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
        //at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
        //at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
        //at java.lang.reflect.Constructor.newInstance(Constructor.java:422)
        //at org.springframework.beans.BeanUtils.instantiateClass(BeanUtils.java:148)
        //... 49 more

        AppViewSubmitter myRunnable = new AppViewSubmitter(trackingId, pluginVersion, teamCityVersion);
        Thread thread = new Thread(myRunnable);
        thread.start();
    }

    public void postEvent(EventCategory eventCategory, EventAction eventAction){
        LOG.info(String.format("Analytics event - %s: %s", eventCategory.name(), eventAction.name()));
        GoogleAnalytics ga = new GoogleAnalytics(trackingId);
        ga.postAsync(new EventHit(eventCategory.name(), eventAction.name()));
    }

    public void postException(Exception e) {
        LOG.info(String.format("Analytics exception - %s", e.getMessage()));
        GoogleAnalytics ga = new GoogleAnalytics(trackingId);
        ga.postAsync(new ExceptionHit(e.toString()));
    }

    class AppViewSubmitter implements Runnable {
        private final String trackingId;
        private final String pluginVersion;
        private final String teamCityVersion;

        public AppViewSubmitter(String trackingId, String pluginVersion, String teamCityVersion) {
            this.trackingId = trackingId;
            this.pluginVersion = pluginVersion;
            this.teamCityVersion = teamCityVersion;
        }

        public void run() {
            LOG.info(String.format("AppViewSubmitter submitting AppViewHit(%s, %s)", pluginVersion, teamCityVersion));

            try {
                GoogleAnalytics ga = new GoogleAnalytics(trackingId);
                LOG.info(String.format("Created GoogleAnalytics object for %s", trackingId));
                GoogleAnalyticsResponse result = ga.post(new AppViewHit("TeamCityOctopusBuildTrigger", pluginVersion, teamCityVersion));
                LOG.info("Logging AppViewHit returned " + result.toString());
            }
            catch(Throwable e) {
                LOG.error("Error logging AppViewHit", e);
            }
        }
    }
}
