package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.*;
import org.jetbrains.annotations.NotNull;

public class ReleasesProviderImpl implements ReleasesProvider {
    @NotNull
    private static final Logger LOG = Logger.getInstance(ReleasesProviderImpl.class.getName());
    private final HttpContentProviderFactory httpContentProviderFactory;

    public ReleasesProviderImpl(HttpContentProviderFactory httpContentProviderFactory) {
        this.httpContentProviderFactory = httpContentProviderFactory;
    }

    public Releases getReleases(String projectId, Release oldRelease) throws InvalidOctopusApiKeyException, InvalidOctopusUrlException, ProjectNotFoundException, ReleasesProviderException {
        String url = null;

        try {
            HttpContentProvider contentProvider = httpContentProviderFactory.getContentProvider();
            url = contentProvider.getUrl();

            LOG.debug("Getting releases from " + contentProvider.getUrl() + " for project id '" + projectId + "'");

            final String apiResponse = contentProvider.getContent("/api");
            final ApiRootResponse apiRootResponse = new ApiRootResponse(apiResponse);

            //todo: move to a builder pattern for loading up results
            String projectsResponse = contentProvider.getContent(apiRootResponse.projectsApiLink);
            ApiProjectsResponse apiProjectsResponse = new ApiProjectsResponse(projectsResponse);
            Projects projects = apiProjectsResponse.projects;
            while (shouldGetNextProjectsPage(apiProjectsResponse, projects, projectId)) {
                projectsResponse = contentProvider.getContent(apiProjectsResponse.nextLink);
                apiProjectsResponse = new ApiProjectsResponse(projectsResponse);
                Projects newProjects = apiProjectsResponse.projects;
                projects.add(newProjects);
            }
            Project project = projects.getProject(projectId);

            String releasesResponse = contentProvider.getContent(project.releasesApiLink);
            ApiProjectReleasesResponse apiProjectReleasesResponse = new ApiProjectReleasesResponse(releasesResponse);

            Releases newReleases = apiProjectReleasesResponse.releases;

            while (shouldGetNextReleasePage(oldRelease, newReleases, apiProjectReleasesResponse)) {
                releasesResponse = contentProvider.getContent(apiProjectReleasesResponse.nextLink);
                apiProjectReleasesResponse = new ApiProjectReleasesResponse(releasesResponse);
                newReleases.add(apiProjectReleasesResponse.releases);
            }
            return newReleases;
        } catch (InvalidOctopusApiKeyException | InvalidOctopusUrlException | ProjectNotFoundException e) {
            throw e;
        } catch (Throwable e) {
            throw new ReleasesProviderException(String.format("Unexpected exception in ReleasesProviderImpl, while attempting to get releases from %s: %s", url, e), e);
        }
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
