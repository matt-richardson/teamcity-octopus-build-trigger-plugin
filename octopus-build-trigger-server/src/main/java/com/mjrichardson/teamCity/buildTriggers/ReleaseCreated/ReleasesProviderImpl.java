package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.*;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class ReleasesProviderImpl implements ReleasesProvider {
    @NotNull
    private static final Logger LOG = Logger.getInstance(ReleasesProviderImpl.class.getName());
    private final HttpContentProviderFactory httpContentProviderFactory;
    private final AnalyticsTracker analyticsTracker;

    public ReleasesProviderImpl(HttpContentProviderFactory httpContentProviderFactory, AnalyticsTracker analyticsTracker) {
        this.httpContentProviderFactory = httpContentProviderFactory;
        this.analyticsTracker = analyticsTracker;
    }

    public Releases getReleases(String projectId, Release oldRelease) throws InvalidOctopusApiKeyException, InvalidOctopusUrlException, ProjectNotFoundException, ReleasesProviderException {
        String url = null;

        try {
            HttpContentProvider contentProvider = httpContentProviderFactory.getContentProvider();
            url = contentProvider.getUrl();

            LOG.debug("Getting releases from " + contentProvider.getUrl() + " for project id '" + projectId + "'");

            final ApiRootResponse apiRootResponse = getApiRootResponse(contentProvider);
            final Project project = getProject(projectId, contentProvider, apiRootResponse);
            return getReleases(oldRelease, contentProvider, project);

        } catch (InvalidOctopusApiKeyException | InvalidOctopusUrlException | ProjectNotFoundException e) {
            throw e;
        } catch (Throwable e) {
            throw new ReleasesProviderException(String.format("Unexpected exception in ReleasesProviderImpl, while attempting to get releases from %s: %s", url, e), e);
        }
    }

    private Releases getReleases(Release oldRelease, HttpContentProvider contentProvider, Project project) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException, InvalidCacheConfigurationException {
        String releasesResponse = contentProvider.getContent(CacheManager.CacheNames.ApiProjectsReleases, project.releasesApiLink);
        ApiProjectReleasesResponse apiProjectReleasesResponse = new ApiProjectReleasesResponse(releasesResponse);

        Releases newReleases = apiProjectReleasesResponse.releases;

        while (shouldGetNextReleasePage(oldRelease, newReleases, apiProjectReleasesResponse)) {
            releasesResponse = contentProvider.getContent(CacheManager.CacheNames.ApiProjectsReleases, apiProjectReleasesResponse.nextLink);
            apiProjectReleasesResponse = new ApiProjectReleasesResponse(releasesResponse);
            newReleases.add(apiProjectReleasesResponse.releases);
        }
        return newReleases;
    }

    private Project getProject(String projectId, HttpContentProvider contentProvider, ApiRootResponse apiRootResponse) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException, InvalidCacheConfigurationException {
        String projectsResponse = contentProvider.getContent(CacheManager.CacheNames.ApiProjects, apiRootResponse.projectsApiLink);
        ApiProjectsResponse apiProjectsResponse = new ApiProjectsResponse(projectsResponse);
        Projects projects = apiProjectsResponse.projects;
        while (shouldGetNextProjectsPage(apiProjectsResponse, projects, projectId)) {
            projectsResponse = contentProvider.getContent(CacheManager.CacheNames.ApiProjects, apiProjectsResponse.nextLink);
            apiProjectsResponse = new ApiProjectsResponse(projectsResponse);
            Projects newProjects = apiProjectsResponse.projects;
            projects.add(newProjects);
        }
        return projects.getProject(projectId);
    }

    @NotNull
    private ApiRootResponse getApiRootResponse(HttpContentProvider contentProvider) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException, InvalidCacheConfigurationException {
        final String apiResponse = contentProvider.getContent(CacheManager.CacheNames.ApiRoot, "/api");
        return new ApiRootResponse(apiResponse, analyticsTracker);
    }

    private boolean shouldGetNextProjectsPage(ApiProjectsResponse apiProjectsResponse, Projects projects, String projectId) {
        if (projects.isEmpty())
            return false;
        if (projects.contains(projectId))
            return false;
        if (apiProjectsResponse.nextLink == null)
            return false;
        return true;
    }

    private boolean shouldGetNextReleasePage(Release oldRelease, Releases newReleases, ApiProjectReleasesResponse apiProjectReleasesResponse) {
        if (newReleases.isEmpty())
            return false;
        if (newReleases.contains(oldRelease))
            return false;
        if (apiProjectReleasesResponse.nextLink == null)
            return false;
        return true;
    }
}
