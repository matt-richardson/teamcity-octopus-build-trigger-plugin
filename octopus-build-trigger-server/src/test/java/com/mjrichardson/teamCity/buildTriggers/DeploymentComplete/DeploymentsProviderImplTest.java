package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeContentProviderFactory;
import com.mjrichardson.teamCity.buildTriggers.*;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeMetricRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@Test
public class DeploymentsProviderImplTest {
    final String octopusUrl = "http://baseUrl";
    final String octopusApiKey = "API-key";
    final String realOctopusUrl = "http://windows10vm.local/";
    final String realOctopusApiKey = "API-H3CUOOWJ1XMWBUHSMASYIPAW20";

    static String ProjectWithNoDeployments = "Projects-23";
    static String ProjectWithLatestDeploymentSuccessful = "Projects-24";
    static String ProjectWithNoSuccessfulDeployments = "Projects-25";
    static String ProjectWithMultipleEnvironments = "Projects-28";
    static String ProjectWithMultipleEnvironmentsAndMostRecentDeploymentSuccessful = "Projects-27";
    static String ProjectWithMultipleEnvironmentsAndNoDeploymentsToOneEnvironment = "Projects-161";
    static String ProjectWithNoRecentSuccessfulDeployments = "Projects-26";
    static String ProjectWithNoReleases = "Projects-101";
    static String ProjectThatDoesNotExist = "Projects-00";
    static String ProjectWithManyDeploymentsWhereAllHaveFailed = "Projects-153";

    @Test(groups = {"needs-real-server"})
    public void get_deployments_from_real_server() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(realOctopusUrl, realOctopusApiKey, OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds(), new FakeCacheManager(), new FakeMetricRegistry());
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldEnvironments);
        Assert.assertNotNull(newEnvironments);
    }

    public void get_deployments_from_empty_start() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 1);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment.environmentId, "Environments-1");

        //2015-12-08T08:09:39.624+00:00
        Assert.assertEquals(environment.latestDeployment.toString(),
                new OctopusDate(2016, 1, 21, 13, 31, 56, 22).toString(),
                "Latest deployment is not as expected");
        //2015-11-12T09:22:00.865+00:00
        Assert.assertEquals(environment.latestSuccessfulDeployment.toString(),
                new OctopusDate(2016, 1, 21, 13, 31, 56, 22).toString(),
                "Latest successful deployment is not as expected");
    }

    public void get_deployments_from_empty_start_with_no_releases() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithNoReleases, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 1);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(1970, 1, 1), new OctopusDate(1970, 1, 1), "", "", "", ""));
    }

    public void get_deployments_from_empty_start_with_no_deployments() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithNoDeployments, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 1);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(1970, 1, 1), new OctopusDate(1970, 1, 1), "", "", "", ""));
    }

    @Test(expectedExceptions = ProjectNotFoundException.class)
    public void get_deployments_with_invalid_project() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();

        deploymentsProviderImpl.getDeployments(ProjectThatDoesNotExist, oldEnvironments);
    }

    @Test(expectedExceptions = InvalidOctopusUrlException.class)
    public void get_deployments_with_octopus_url_with_invalid_host() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory("http://octopus.example.com", octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();

        deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldEnvironments);
    }

    @Test(expectedExceptions = InvalidOctopusUrlException.class)
    public void get_deployments_with_octopus_url_with_invalid_path() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl + "/not-an-octopus-instance", octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();

        deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldEnvironments);
    }

    @Test(expectedExceptions = InvalidOctopusApiKeyException.class)
    public void get_deployments_with_invalid_octopus_api_key() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, "invalid-api-key");
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();

        deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldEnvironments);
    }

    @Test(expectedExceptions = DeploymentsProviderException.class)
    public void rethrows_throwable_exceptions_as_deployment_provider_exception() throws ProjectNotFoundException, DeploymentsProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(new OutOfMemoryError());
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();

        deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldEnvironments);
    }

    public void get_deployments_when_up_to_date() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = Environments.Parse("Environments-1;2016-01-21T13:31:56.022+00:00;2016-01-21T13:31:56.022+00:00;the-release-id;the-deployment-id;the-version;the-project-id");
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithLatestDeploymentSuccessful, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 1);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2016, 1, 21, 13, 31, 56, 22), new OctopusDate(2016, 1, 21, 13, 31, 56, 22), "Releases-63", "Deployments-81", "0.0.1", "Projects-24"));
    }

    public void get_deployments_when_no_successful_deployments_have_occurred() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithNoSuccessfulDeployments, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 1);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2016, 1, 21, 13, 32, 59, 991), new OctopusDate(1970, 1, 1), "Releases-64", "Deployments-82", "0.0.1", "Projects-25"));
    }

    public void get_deployments_when_no_successful_deployments_on_first_page_of_results() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithNoRecentSuccessfulDeployments, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 1);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2016, 1, 21, 14, 18, 1, 887), new OctopusDate(2016, 1, 21, 13, 35, 27, 179), "Releases-65", "Deployments-83", "0.0.1", "Projects-26"));
    }

    public void get_deployments_when_multiple_environments() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithMultipleEnvironments, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 2);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2016, 1, 21, 14, 26, 14, 747), new OctopusDate(2016, 1, 21, 14, 25, 40, 247), "Releases-69", "Deployments-117", "0.0.1", "Projects-28"));
        environment = newEnvironments.getEnvironment("Environments-21");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-21", new OctopusDate(2016, 1, 21, 14, 25, 53, 700), new OctopusDate(2016, 1, 21, 14, 25, 53, 700), "Releases-69", "Deployments-118", "0.0.1", "Projects-28"));
    }

    public void get_deployments_when_no_releases() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithNoReleases, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 1);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(1970, 1, 1), new OctopusDate(1970, 1, 1), "", "", "", ""));
    }

    public void get_deployments_when_multiple_environments_and_no_deployments_to_one_of_the_environments() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        FakeAnalyticsTracker analyticsTracker = new FakeAnalyticsTracker();
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, analyticsTracker);
        Environments oldEnvironments = new Environments();
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithMultipleEnvironmentsAndNoDeploymentsToOneEnvironment, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 2);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2015, 4, 9, 7, 42, 13, 557), new OctopusDate(2015, 4, 9, 7, 42, 13, 557), "Releases-303", "Deployments-303", "0.0.3", "Projects-161"));
        environment = newEnvironments.getEnvironment("Environments-21");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-21", new OctopusDate(1970, 1, 1), new OctopusDate(1970, 1, 1), "", "", "", ""));
        Assert.assertEquals(analyticsTracker.eventAction, AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedSameResults);
    }

    public void get_deployments_when_multiple_environments_and_no_deployments_to_one_of_the_environments_on_the_second_call() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        FakeAnalyticsTracker analyticsTracker = new FakeAnalyticsTracker();
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, analyticsTracker);
        Environments oldEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithMultipleEnvironmentsAndNoDeploymentsToOneEnvironment, new Environments());
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithMultipleEnvironmentsAndNoDeploymentsToOneEnvironment, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 2);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2015, 4, 9, 7, 42, 13, 557), new OctopusDate(2015, 4, 9, 7, 42, 13, 557), "Releases-303", "Deployments-303", "0.0.3", "Projects-161"));
        environment = newEnvironments.getEnvironment("Environments-21");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-21", new OctopusDate(1970, 1, 1), new OctopusDate(1970, 1, 1), "", "", "", ""));
        Assert.assertEquals(analyticsTracker.eventAction, AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedSameResults);
    }

    public void when_there_are_two_new_deployments_since_last_check_it_returns_only_one() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        final String oldData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00;Releases-69;Deployments-115;0.0.1;Projects-28|" +
                               "Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00;Releases-69;Deployments-116;0.0.1;Projects-28";
        Environments oldEnvironments = Environments.Parse(oldData);
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithMultipleEnvironments, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 2);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2016, 1, 21, 14, 26, 14, 747), new OctopusDate(2016, 1, 21, 14, 25, 40, 247), "Releases-69", "Deployments-117", "0.0.1", "Projects-28"));
        environment = newEnvironments.getEnvironment("Environments-21");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-21", new OctopusDate(2016, 1, 21, 14, 25, 53, 700), new OctopusDate(2016, 1, 21, 14, 25, 53, 700), "Releases-69", "Deployments-118", "0.0.1", "Projects-28"));

        final Environments trimmedEnvironments = newEnvironments.trimToOnlyHaveMaximumOneChangedEnvironment(oldEnvironments);
        Assert.assertEquals(trimmedEnvironments.size(), 2);
        environment = trimmedEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2016, 1, 21, 14, 26, 14, 747), new OctopusDate(2016, 1, 21, 14, 25, 40, 247), "Releases-69", "Deployments-117", "0.0.1", "Projects-28"));
        environment = trimmedEnvironments.getEnvironment("Environments-21");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-21", new OctopusDate(2016, 1, 20, 14, 0, 0, 0), new OctopusDate(2016, 1, 20, 14, 0, 0, 0), "Releases-69", "Deployments-116", "0.0.1", "Projects-28"));
    }

    public void get_deployments_when_multiple_environments_with_most_recent_deployment_successful() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithMultipleEnvironmentsAndMostRecentDeploymentSuccessful, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 2);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2016, 1, 21, 14, 24, 30, 935), new OctopusDate(2016, 1, 21, 14, 24, 30, 935), "Releases-67", "Deployments-114", "0.0.1", "Projects-27"));
        environment = newEnvironments.getEnvironment("Environments-21");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-21", new OctopusDate(2016, 1, 21, 14, 24, 10, 872), new OctopusDate(2016, 1, 21, 14, 24, 10, 872), "Releases-67", "Deployments-115", "0.0.1", "Projects-27"));
    }

    public void get_deployments_when_project_has_many_deployments() throws Exception {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        Environments oldEnvironments = new Environments();
        Environments newEnvironments = deploymentsProviderImpl.getDeployments(ProjectWithManyDeploymentsWhereAllHaveFailed, oldEnvironments);
        Assert.assertEquals(newEnvironments.size(), 1);
        Environment environment = newEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2016, 3, 9, 22, 26, 43, 504), new NullOctopusDate(), "Releases-244", "Deployments-245", "0.0.31", "Projects-153"));
    }

    public void determine_outcome_of_fallback_handles_more_responses() {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        FakeAnalyticsTracker fakeAnalyticsTracker = new FakeAnalyticsTracker();
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, fakeAnalyticsTracker);

        Environments environmentsFromProgressionApi = new Environments();
        environmentsFromProgressionApi.addEnvironment("Environment-1");
        Environments environmentsFromDeploymentsApi = new Environments();
        environmentsFromDeploymentsApi.addEnvironment("Environment-1");
        environmentsFromDeploymentsApi.addEnvironment("Environment-2");

        AnalyticsTracker.EventAction result = deploymentsProviderImpl.determineOutcomeOfFallback(environmentsFromProgressionApi, environmentsFromDeploymentsApi);
        Assert.assertEquals(result, AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedMoreEnvironments);
    }

    public void determine_outcome_of_fallback_handles_fewer_responses() {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        FakeAnalyticsTracker fakeAnalyticsTracker = new FakeAnalyticsTracker();
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, fakeAnalyticsTracker);

        Environments environmentsFromProgressionApi = new Environments();
        environmentsFromProgressionApi.addEnvironment("Environment-1");
        environmentsFromProgressionApi.addEnvironment("Environment-2");
        Environments environmentsFromDeploymentsApi = new Environments();
        environmentsFromDeploymentsApi.addEnvironment("Environment-1");

        AnalyticsTracker.EventAction result = deploymentsProviderImpl.determineOutcomeOfFallback(environmentsFromProgressionApi, environmentsFromDeploymentsApi);
        Assert.assertEquals(result, AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedFewerEnvironments);
    }

    public void determine_outcome_of_fallback_handles_identical_responses() throws ParseException, NeedToDeleteAndRecreateTrigger {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        FakeAnalyticsTracker fakeAnalyticsTracker = new FakeAnalyticsTracker();
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, fakeAnalyticsTracker);

        final String oldData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id";
        Environments environmentsFromProgressionApi = Environments.Parse(oldData);
        Environments environmentsFromDeploymentsApi = Environments.Parse(oldData);

        AnalyticsTracker.EventAction result = deploymentsProviderImpl.determineOutcomeOfFallback(environmentsFromProgressionApi, environmentsFromDeploymentsApi);
        Assert.assertEquals(result, AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedSameResults);
    }

    public void determine_outcome_of_fallback_handles_different_environments() throws ParseException, NeedToDeleteAndRecreateTrigger {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        FakeAnalyticsTracker fakeAnalyticsTracker = new FakeAnalyticsTracker();
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, fakeAnalyticsTracker);

        final String oldData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id";
        Environments environmentsFromProgressionApi = Environments.Parse(oldData);
        final String newData = "Environments-2;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id";
        Environments environmentsFromDeploymentsApi = Environments.Parse(newData);

        AnalyticsTracker.EventAction result = deploymentsProviderImpl.determineOutcomeOfFallback(environmentsFromProgressionApi, environmentsFromDeploymentsApi);
        Assert.assertEquals(result, AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedDifferentEnvironments);
    }

    public void determine_outcome_of_fallback_handles_response_with_newer_latest_deployment_date() throws ParseException, NeedToDeleteAndRecreateTrigger {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        FakeAnalyticsTracker fakeAnalyticsTracker = new FakeAnalyticsTracker();
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, fakeAnalyticsTracker);

        final String progressionApiResult = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id";
        Environments environmentsFromProgressionApi = Environments.Parse(progressionApiResult);
        final String deploymentsApiResult = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id|Environments-21;2016-01-20T15:00:00.000+00:00;2016-01-20T14:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id";
        Environments environmentsFromDeploymentsApi = Environments.Parse(deploymentsApiResult);

        AnalyticsTracker.EventAction result = deploymentsProviderImpl.determineOutcomeOfFallback(environmentsFromProgressionApi, environmentsFromDeploymentsApi);
        Assert.assertEquals(result, AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedBetterInformation);
    }

    public void determine_outcome_of_fallback_handles_response_with_newer_successful_latest_deployment_date() throws ParseException, NeedToDeleteAndRecreateTrigger {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        FakeAnalyticsTracker fakeAnalyticsTracker = new FakeAnalyticsTracker();
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, fakeAnalyticsTracker);

        final String progressionApiResult = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id";
        Environments environmentsFromProgressionApi = Environments.Parse(progressionApiResult);
        final String deploymentsApiResult = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id|Environments-21;2016-01-20T15:00:00.000+00:00;2016-01-20T15:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id";
        Environments environmentsFromDeploymentsApi = Environments.Parse(deploymentsApiResult);

        AnalyticsTracker.EventAction result = deploymentsProviderImpl.determineOutcomeOfFallback(environmentsFromProgressionApi, environmentsFromDeploymentsApi);
        Assert.assertEquals(result, AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedBetterInformation);
    }

    public void determine_outcome_of_fallback_handles_response_with_older_latest_deployment_date() throws ParseException, NeedToDeleteAndRecreateTrigger {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        FakeAnalyticsTracker fakeAnalyticsTracker = new FakeAnalyticsTracker();
        DeploymentsProviderImpl deploymentsProviderImpl = new DeploymentsProviderImpl(contentProviderFactory, fakeAnalyticsTracker);

        final String progressionApiResult = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id";
        Environments environmentsFromProgressionApi = Environments.Parse(progressionApiResult);
        final String deploymentsApiResult = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id|Environments-21;2016-01-20T13:00:00.000+00:00;2016-01-20T13:00:00.000+00:00;the-release-id;the-deployment-id;the-version;the-project-id";
        Environments environmentsFromDeploymentsApi = Environments.Parse(deploymentsApiResult);

        AnalyticsTracker.EventAction result = deploymentsProviderImpl.determineOutcomeOfFallback(environmentsFromProgressionApi, environmentsFromDeploymentsApi);
        Assert.assertEquals(result, AnalyticsTracker.EventAction.FallBackToDeploymentsApiProducedWorseResults);
    }
}
