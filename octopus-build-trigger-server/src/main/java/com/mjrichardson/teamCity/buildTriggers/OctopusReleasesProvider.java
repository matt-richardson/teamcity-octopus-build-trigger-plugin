package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


public class OctopusReleasesProvider {
    private final Logger LOG;
    private final HttpContentProvider contentProvider;

    public OctopusReleasesProvider(String octopusUrl, String apiKey, Integer connectionTimeout, Logger log) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this(new HttpContentProviderImpl(log, octopusUrl, apiKey, connectionTimeout), log);
    }

    public OctopusReleasesProvider(HttpContentProvider contentProvider, Logger log)
    {
        this.contentProvider = contentProvider;
        this.LOG = log;
    }

    public Releases getReleases(String projectId, Releases oldReleases) throws InvalidOctopusApiKeyException, InvalidOctopusUrlException, ProjectNotFoundException, OctopusReleasesProviderException {
        //get {octopusurl}/api
        //parse out releases url
        //call release url for project id

        try {
            LOG.debug("ReleaseCreatedBuildTrigger: Getting releases from " + contentProvider.getUrl() + " for project id '" + projectId + "'");

            final String apiResponse = contentProvider.getContent("/api");
            final ApiRootResponse apiRootResponse = new ApiRootResponse(apiResponse);

            String releasesResponse = contentProvider.getContent("/api/projects/" + projectId + "/releases"); //todo: parse properly
            ApiReleaseResponse apiReleaseResponse = new ApiReleaseResponse(releasesResponse);

            Releases newReleases = apiReleaseResponse.releases;

            while (shouldGetNextPage(oldReleases, newReleases, apiReleaseResponse)) {
                releasesResponse = contentProvider.getContent(apiReleaseResponse.nextLink);
                apiReleaseResponse = new ApiReleaseResponse(releasesResponse);
                newReleases.Append(apiReleaseResponse.releases);
            }
            return newReleases;
        }
        catch (InvalidOctopusApiKeyException e) {
            throw e;
        }
        catch (InvalidOctopusUrlException e) {
            throw e;
        }
        catch (ProjectNotFoundException e) {
            throw e;
        }
        catch (Throwable e) {
            throw new OctopusReleasesProviderException("URL " + contentProvider.getUrl() + ": " + e, e);
        }
    }

    private boolean shouldGetNextPage(Releases oldReleases, Releases newReleases, ApiReleaseResponse apiReleaseResponse) {
        if (newReleases.isEmpty())
            return false;
        if (apiReleaseResponse.nextLink == null)
            return false;
        if (newReleases.overlapsWith(oldReleases))
            return false;
        return true;
    }
}
