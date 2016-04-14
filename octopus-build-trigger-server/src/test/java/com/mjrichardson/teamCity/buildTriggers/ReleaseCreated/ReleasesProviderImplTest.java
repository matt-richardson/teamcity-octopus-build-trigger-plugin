package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeContentProviderFactory;
import com.mjrichardson.teamCity.buildTriggers.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class ReleasesProviderImplTest {
    final String octopusUrl = "http://baseUrl";
    final String octopusApiKey = "API-key";
    final String realOctopusUrl = "http://windows10vm.local/";
    final String realOctopusApiKey = "API-H3CUOOWJ1XMWBUHSMASYIPAW20";

    static String ProjectWithLatestReleaseSuccessful = "Projects-24";
    static String ProjectWithNoReleases = "Projects-101";
    static String ProjectWithManyReleases = "Projects-103";
    static String ProjectThatDoesNotExist = "Projects-00";

    @Test(groups = {"needs-real-server"})
    public void get_releases_from_real_server() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ProjectNotFoundException, ReleasesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(realOctopusUrl, realOctopusApiKey, OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds(), new FakeCacheManager());
        ReleasesProviderImpl ReleasesProviderImpl = new ReleasesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Release oldRelease = new NullRelease();
        Releases newReleases = ReleasesProviderImpl.getReleases(ProjectWithLatestReleaseSuccessful, oldRelease);
        Assert.assertNotNull(newReleases);
    }

    public void get_releases_from_empty_start() throws ProjectNotFoundException, ReleasesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        ReleasesProviderImpl ReleasesProviderImpl = new ReleasesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Release oldRelease = new NullRelease();
        Releases newReleases = ReleasesProviderImpl.getReleases(ProjectWithLatestReleaseSuccessful, oldRelease);
        Assert.assertEquals(newReleases.size(), 1);
        Release release = newReleases.getNextRelease(oldRelease);
        Assert.assertNotNull(release);
        Assert.assertEquals(release.releaseId, "Releases-63");
        Assert.assertEquals(release.assembledDate, new OctopusDate(2016, 1, 21, 13, 31, 50, 304));
        Assert.assertEquals(release.version, "0.0.1");
    }

    public void get_releases_from_empty_start_with_no_releases() throws ProjectNotFoundException, ReleasesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        ReleasesProviderImpl ReleasesProviderImpl = new ReleasesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Release oldRelease = new NullRelease();
        Releases newReleases = ReleasesProviderImpl.getReleases(ProjectWithNoReleases, oldRelease);
        Assert.assertEquals(newReleases.size(), 0);
    }

    @Test(expectedExceptions = ProjectNotFoundException.class)
    public void get_releases_with_invalid_project() throws ProjectNotFoundException, ReleasesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        ReleasesProviderImpl ReleasesProviderImpl = new ReleasesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Release oldRelease = new NullRelease();

        ReleasesProviderImpl.getReleases(ProjectThatDoesNotExist, oldRelease);
    }

    @Test(expectedExceptions = InvalidOctopusUrlException.class)
    public void get_releases_with_octopus_url_with_invalid_host() throws ProjectNotFoundException, ReleasesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory("http://octopus.example.com", octopusApiKey);
        ReleasesProviderImpl ReleasesProviderImpl = new ReleasesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Release oldRelease = new NullRelease();

        ReleasesProviderImpl.getReleases(ProjectWithLatestReleaseSuccessful, oldRelease);
    }

    @Test(expectedExceptions = InvalidOctopusUrlException.class)
    public void get_releases_with_octopus_url_with_invalid_path() throws ProjectNotFoundException, ReleasesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl + "/not-an-octopus-instance", octopusApiKey);
        ReleasesProviderImpl ReleasesProviderImpl = new ReleasesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Release oldRelease = new NullRelease();

        ReleasesProviderImpl.getReleases(ProjectWithLatestReleaseSuccessful, oldRelease);
    }

    @Test(expectedExceptions = InvalidOctopusApiKeyException.class)
    public void get_releases_with_invalid_octopus_api_key() throws ProjectNotFoundException, ReleasesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, "invalid-api-key");
        ReleasesProviderImpl ReleasesProviderImpl = new ReleasesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Release oldRelease = new NullRelease();

        ReleasesProviderImpl.getReleases(ProjectWithLatestReleaseSuccessful, oldRelease);
    }

    @Test(expectedExceptions = ReleasesProviderException.class)
    public void rethrows_throwable_exceptions_as_deployment_provider_exception() throws ProjectNotFoundException, ReleasesProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(new OutOfMemoryError());
        ReleasesProviderImpl ReleasesProviderImpl = new ReleasesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Release oldRelease = new NullRelease();

        ReleasesProviderImpl.getReleases(ProjectWithLatestReleaseSuccessful, oldRelease);
    }

    public void get_releases_when_up_to_date() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        ReleasesProviderImpl ReleasesProviderImpl = new ReleasesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        Release oldRelease = new Release("Releases-63", new OctopusDate(2016, 1, 21, 13, 31, 50, 304), "0.0.1", "the-project-id");
        Releases newReleases = ReleasesProviderImpl.getReleases(ProjectWithLatestReleaseSuccessful, oldRelease);
        Assert.assertEquals(newReleases.size(), 1);
        Release release = newReleases.getNextRelease(oldRelease);
        Assert.assertNotNull(release);
        Assert.assertEquals(release, oldRelease);
    }

    public void get_releases_when_more_than_one_page_of_releases_and_current_known_release_is_on_second_page() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        ReleasesProviderImpl ReleasesProviderImpl = new ReleasesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        Release oldRelease = new Release("Releases-147", new OctopusDate(2016, 2, 22, 21, 6, 39, 43), "0.0.1", "the-project-id");
        Releases newReleases = ReleasesProviderImpl.getReleases(ProjectWithManyReleases, oldRelease);
        Assert.assertEquals(newReleases.size(), 31);
        Release release = newReleases.getNextRelease(oldRelease);
        Assert.assertNotNull(release);
        Assert.assertEquals(release.releaseId, "Releases-148");
        Assert.assertEquals(release.version, "0.0.2");
        Assert.assertEquals(release.assembledDate, new OctopusDate(2016, 2, 22, 21, 6, 45, 598));
    }

    public void get_releases_when_more_than_one_page_of_releases_and_old_release_not_found() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        ReleasesProviderImpl ReleasesProviderImpl = new ReleasesProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        Release oldRelease = new NullRelease();
        Releases newReleases = ReleasesProviderImpl.getReleases(ProjectWithManyReleases, oldRelease);
        Assert.assertEquals(newReleases.size(), 31);
        Release release = newReleases.getNextRelease(oldRelease);
        Assert.assertNotNull(release);
        Assert.assertEquals(release.releaseId, "Releases-147");
        Assert.assertEquals(release.version, "0.0.1");
        Assert.assertEquals(release.assembledDate, new OctopusDate(2016, 2, 22, 21, 6, 39, 43));
    }
}
