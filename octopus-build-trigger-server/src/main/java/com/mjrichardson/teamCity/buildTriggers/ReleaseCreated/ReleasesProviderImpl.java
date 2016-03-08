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
            final ApiProjectsResponse apiProjectsResponse = new ApiProjectsResponse(projectsResponse);
            while (shouldGetNextProjectsPage(apiProjectsResponse, projectId)) {
                projectsResponse = contentProvider.getContent(apiProjectsResponse.nextLink);
                apiProjectsResponse.add(new ApiProjectReleasesResponse(projectsResponse));
            }
            Project project = apiProjectsResponse.getProject(projectId);
            String releasesResponse = contentProvider.getContent(project.releasesApiLink);
            ApiProjectReleasesResponse apiProjectReleasesResponse = new ApiProjectReleasesResponse(releasesResponse);

            Releases newReleases = apiProjectReleasesResponse.releases;

            while (shouldGetNextReleasePage(oldRelease, newReleases, apiProjectReleasesResponse)) {
                releasesResponse = contentProvider.getContent(apiProjectReleasesResponse.nextLink);
                apiProjectReleasesResponse = new ApiProjectReleasesResponse(releasesResponse);
                newReleases.add(apiProjectReleasesResponse.releases);
            }
            return newReleases;
        } catch (InvalidOctopusApiKeyException e) {
            throw e;
        } catch (InvalidOctopusUrlException e) {
            throw e;
        } catch (ProjectNotFoundException e) {
            throw e;
        } catch (Throwable e) {
            throw new ReleasesProviderException(String.format("Unexpected exception in ReleasesProviderImpl, while attempting to get releases from %s: %s", url, e), e);
        }
    }

    private boolean shouldGetNextProjectsPage(ApiProjectsResponse apiProjectsResponse, String projectId) {
        if (apiProjectsResponse.isEmpty())
            return false;
        if (apiProjectsResponse.projects.contains(projectId))
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
