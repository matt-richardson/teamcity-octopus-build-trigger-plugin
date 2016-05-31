package com.mjrichardson.teamCity.buildTriggers;

import com.codahale.metrics.MetricRegistry;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpdateChecker {
    @NotNull
    private static final Logger LOG = Logger.getInstance(UpdateChecker.class.getName());
    @NotNull
    private final PluginDescriptor pluginDescriptor;
    @NotNull
    private final HttpContentProvider httpContentProvider;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static String updateUrl;
    private static String latestVersion;
    private static String currentVersion;
    public static boolean updateIsAvailable;
    private final BuildTriggerProperties buildTriggerProperties;
    @NotNull
    private final EventDispatcher<BuildServerListener> eventDispatcher;

    //used by spring
    public UpdateChecker(@NotNull final PluginDescriptor pluginDescriptor,
                         CacheManager cacheManager,
                         MetricRegistry metricRegistry,
                         BuildTriggerProperties buildTriggerProperties,
                         @NotNull EventDispatcher<BuildServerListener> eventDispatcher) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ProjectNotFoundException, InvalidCacheConfigurationException, IOException, UnexpectedResponseCodeException, ParseException, URISyntaxException, InvalidOctopusUrlException, InvalidOctopusApiKeyException {
        this.eventDispatcher = eventDispatcher;
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(null, null,
                buildTriggerProperties, cacheManager, metricRegistry);
        HttpContentProvider contentProvider = contentProviderFactory.getContentProvider();
        this.pluginDescriptor = pluginDescriptor;
        this.httpContentProvider = contentProvider;
        this.buildTriggerProperties = buildTriggerProperties;
        setupScheduledTask();
    }

    UpdateChecker(@NotNull final PluginDescriptor pluginDescriptor,
                  @NotNull final HttpContentProvider httpContentProvider,
                  @NotNull final BuildTriggerProperties buildTriggerProperties,
                  @NotNull EventDispatcher<BuildServerListener> eventDispatcher) throws IOException, InvalidCacheConfigurationException, NoSuchAlgorithmException, URISyntaxException, KeyStoreException, ParseException, InvalidOctopusUrlException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, ProjectNotFoundException, KeyManagementException {
        this.pluginDescriptor = pluginDescriptor;
        this.httpContentProvider = httpContentProvider;
        this.buildTriggerProperties = buildTriggerProperties;
        this.eventDispatcher = eventDispatcher;
        setupScheduledTask();
    }

    private void setupScheduledTask() throws IOException, InvalidCacheConfigurationException, NoSuchAlgorithmException, URISyntaxException, InvalidOctopusApiKeyException, ParseException, InvalidOctopusUrlException, UnexpectedResponseCodeException, KeyStoreException, ProjectNotFoundException, KeyManagementException {
        eventDispatcher.addListener(new BuildServerAdapter() {
            public void serverShutdown() {
                LOG.debug("Server shutdown initiated - shutting down updateChecker");
                if(scheduler != null) {
                    scheduler.shutdown();
                }
                LOG.debug("Server shutdown initiated - updateChecker shutdown complete");
            }
        });
        scheduler.scheduleAtFixedRate(() -> checkForUpdates(), 60, 60, TimeUnit.MINUTES);
        checkForUpdates();
    }

    private synchronized void checkForUpdates()  {
        if (!buildTriggerProperties.isUpdateCheckEnabled())
            return;
        UUID correlationId = UUID.randomUUID();
        try {
            String currentVersion = pluginDescriptor.getPluginVersion();
            if (currentVersion == null) {
                LOG.warn(String.format("%s: Update check failed, couldn't get pluginDescriptor.getPluginVersion()", correlationId));
                UpdateChecker.updateIsAvailable = false;
                return;
            }
            String apiResponse = httpContentProvider.getContent(CacheManager.CacheNames.GitHubLatestRelease,
                    new URI("https://api.github.com/repos/matt-richardson/teamcity-octopus-build-trigger-plugin/releases/latest"), correlationId);
            GitHubApiReleaseResponse githubApiReleaseResponse = new GitHubApiReleaseResponse(apiResponse);

            DefaultArtifactVersion gitHubVersion = new DefaultArtifactVersion(githubApiReleaseResponse.tagName);
            DefaultArtifactVersion pluginVersion = new DefaultArtifactVersion(currentVersion);

            UpdateChecker.currentVersion = pluginVersion.toString();
            UpdateChecker.latestVersion = gitHubVersion.toString();
            UpdateChecker.updateUrl = githubApiReleaseResponse.htmlUrl;
            UpdateChecker.updateIsAvailable = gitHubVersion.compareTo(pluginVersion) > 0;

            LOG.info(String.format("%s: Update check - current version is '%s', latest version is '%s' -> update available: %s", correlationId, pluginVersion, gitHubVersion, updateIsAvailable));
        }
        catch (Exception ex) {
            LOG.warn(String.format("%s: Update check failed, pretending no updates available", correlationId), ex);
            UpdateChecker.updateIsAvailable = false;
        }
    }

    public static String getUpdateUrl() {
        return updateUrl;
    }

    public static String getLatestVersion() {
        return latestVersion;
    }

    public static String getCurrentVersion() {
        return currentVersion;
    }

    public static boolean isUpdateAvailable() throws IOException, InvalidCacheConfigurationException, NoSuchAlgorithmException, URISyntaxException, InvalidOctopusApiKeyException, ParseException, InvalidOctopusUrlException, UnexpectedResponseCodeException, KeyStoreException, ProjectNotFoundException, KeyManagementException {
        return updateIsAvailable;
    }
}
