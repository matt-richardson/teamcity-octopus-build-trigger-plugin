/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package matt_richardson.teamCity.buildTriggers.octopusDeploy;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.log.Loggers;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


@Test
public class OctopusDeploymentsProviderTest {
  final String octopusUrl = "http://baseUrl";
  final String octopusApiKey = "API-key";
  final String realOctopusUrl = "http://windows10vm.local/";
  final String realOctopusApiKey = "API-H3CUOOWJ1XMWBUHSMASYIPAW20";

  private static final Logger LOG = Logger.getInstance(Loggers.VCS_CATEGORY + OctopusBuildTrigger.class);

  @Test(enabled = false)
  public void testGetDeploymentsFromRealServer() throws Exception {
    HttpContentProvider contentProvider = new HttpContentProviderImpl(LOG, realOctopusUrl, realOctopusApiKey, OctopusBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT
    );
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments("Project with latest deployment successful", oldDeployments);
    Assert.assertNotNull(newDeployments);
  }

  public void testGetDeploymentsFromEmptyStart() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments("Project with latest deployment successful", oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.environmentId, "Environments-1");

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    //2015-12-08T08:09:39.624+00:00
    Assert.assertEquals(formatter.format(deployment.latestDeployment),
                        formatter.format(new GregorianCalendar(2016, Calendar.JANUARY, 21, 13, 31, 56).getTime()),
                        "Latest deployment is not as expected");
    //2015-11-12T09:22:00.865+00:00
    Assert.assertEquals(formatter.format(deployment.latestSuccessfulDeployment),
                        formatter.format(new GregorianCalendar(2016, Calendar.JANUARY, 21, 13, 31, 56).getTime()),
                        "Latest successful deployment is not as expected");
  }

  public void testGetDeploymentsFromEmptyStartWithNoReleases() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments("Project with no releases", oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;1970-01-01T01:00:00.000+01:00;1970-01-01T01:00:00.000+01:00");
  }

  public void testGetDeploymentsFromEmptyStartWithNoDeployments() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments("Project with no deployments", oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;1970-01-01T01:00:00.000+01:00;1970-01-01T01:00:00.000+01:00");
  }

  @Test(expectedExceptions = ProjectNotFoundException.class)
  public void testGetDeploymentsWithInvalidProject() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments();

    deploymentsProvider.getDeployments("Project that does not exist", oldDeployments);
  }

  @Test(expectedExceptions = InvalidOctopusUrlException.class)
  public void testGetDeploymentsWithOctopusUrlWithInvalidHost() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider("http://octopus.example.com", octopusApiKey);
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments();

    //todo: need another test around HttpContentProviderImpl
    deploymentsProvider.getDeployments("Project with latest deployment successful", oldDeployments);
  }

  @Test(expectedExceptions = InvalidOctopusUrlException.class)
  public void testGetDeploymentsWithOctopusUrlWithInvalidPath() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl + "/not-an-octopus-instance", octopusApiKey);
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments();

    //todo: need another test around HttpContentProviderImpl
    deploymentsProvider.getDeployments("Project with latest deployment successful", oldDeployments);
  }

  @Test(expectedExceptions = InvalidOctopusApiKeyException.class)
  public void testGetDeploymentsWithInvalidOctopusApiKey() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, "invalid-api-key");
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments();

    //todo: need another test around HttpContentProviderImpl
    deploymentsProvider.getDeployments("Project with latest deployment successful", oldDeployments);
  }

  public void testGetDeploymentsWhenUpToDate() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments("Environments-1;2016-01-21T13:31:56.022Z;2016-01-21T13:31:56.022Z");
    Deployments newDeployments = deploymentsProvider.getDeployments("Project with latest deployment successful", oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T13:31:56.022Z;2016-01-21T13:31:56.022Z");
  }

  public void testGetDeploymentsWhenNoSuccessfulDeploymentsHaveOccurred() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments("Project with no successful deployments", oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T13:32:59.991Z;1970-01-01T01:00:00.000+01:00");
  }

  public void testGetDeploymentsWhenNoSuccessfulDeploymentsOnFirstPageOfResults() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments("Project with no recent successful deployments", oldDeployments);
    Assert.assertEquals(newDeployments.length(), 1);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T14:18:01.887Z;2016-01-21T13:35:27.179Z");
  }

  public void testGetDeploymentsWhenMultipleEnvironments() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments("Project with multiple environments", oldDeployments);
    Assert.assertEquals(newDeployments.length(), 2);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T14:26:14.747Z;2016-01-21T14:25:40.247Z");
    deployment = newDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-21;2016-01-21T14:25:53.700Z;2016-01-21T14:25:53.700Z");
  }

  public void testGetDeploymentsWhenMultipleEnvironmentsWithMostRecentDeploymentSuccessful() throws Exception {
    HttpContentProvider contentProvider = new FakeContentProvider(octopusUrl, octopusApiKey);
    OctopusDeploymentsProvider deploymentsProvider = new OctopusDeploymentsProvider(contentProvider, LOG);
    Deployments oldDeployments = new Deployments();
    Deployments newDeployments = deploymentsProvider.getDeployments("Project with multiple environments and most recent deployment successful", oldDeployments);
    Assert.assertEquals(newDeployments.length(), 2);
    Deployment deployment = newDeployments.getDeploymentForEnvironment("Environments-1");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-1;2016-01-21T14:24:30.935Z;2016-01-21T14:24:30.935Z");
    deployment = newDeployments.getDeploymentForEnvironment("Environments-21");
    Assert.assertNotNull(deployment);
    Assert.assertEquals(deployment.toString(), "Environments-21;2016-01-21T14:24:10.872Z;2016-01-21T14:24:10.872Z");
  }

  private class FakeContentProvider implements HttpContentProvider {
    private final String octopusUrl;
    private String octopusApiKey;

    public FakeContentProvider(String octopusUrl, String octopusApiKey) {

      this.octopusUrl = octopusUrl;
      this.octopusApiKey = octopusApiKey;
    }

    public void close(@Nullable Closeable closeable) {
      //no-op
    }

    public String getContent(String uriPath) throws IOException, InvalidOctopusUrlException, InvalidOctopusApiKeyException, URISyntaxException {
      String s = octopusUrl + uriPath;
      if (this.octopusUrl.contains("not-an-octopus-instance") || this.octopusUrl.contains("example.com")) {
        throw new InvalidOctopusUrlException(new URI(s)); //this is a bit odd, but we are just checking to make sure the right exception gets back to the right spot
      }
      if (!this.octopusApiKey.startsWith("API-")) {
        throw new InvalidOctopusApiKeyException(401, "Invalid octopus api key");
      }
      if (s.equals(octopusUrl + "/api")) {
        return "{  \"Application\": \"Octopus Deploy\",  \"Version\": \"3.2.1\",  \"ApiVersion\": \"3.0.0\",  \"FormsLoginEnabled\": true,  \"Links\": {    \"Self\": \"/api\",    \"Accounts\": \"/api/accounts{/id}{?skip}\",    \"ActionTemplates\": \"/api/actiontemplates{/id}{?skip}\",    \"Alerts\": \"/api/alerts{/id}{?skip}\",    \"Artifacts\": \"/api/artifacts{/id}{?skip,regarding}\",    \"Channels\": \"/api/channels{/id}\",    \"Certificates\": \"/api/certificates{/id}{?skip}\",    \"CurrentUser\": \"/api/users/me\",    \"CurrentLicense\": \"/api/licenses/licenses-current\",    \"Dashboard\": \"/api/dashboard\",    \"DashboardConfiguration\": \"/api/dashboardconfiguration\",    \"DashboardDynamic\": \"/api/dashboard/dynamic{?projects,environments,includePrevious}\",    \"DeploymentProcesses\": \"/api/deploymentprocesses{/id}\",    \"Deployments\": \"/api/deployments{/id}{?skip,take,projects,environments,taskState}\",    \"DiscoverMachine\": \"/api/machines/discover{?host,port,type}\",    \"Environments\": \"/api/environments{/id}{?skip}\",    \"EnvironmentSortOrder\": \"/api/environments/sortorder\",    \"Events\": \"/api/events{/id}{?skip,regarding,modifier,user,from,to}\",    \"ExternalSecurityGroups\": \"/api/externalsecuritygroups{/id}{?name}\",    \"Feeds\": \"/api/feeds{/id}{?skip}\",    \"Interruptions\": \"/api/interruptions{/id}{?skip,regarding,pendingOnly}\",    \"Invitations\": \"/api/users/invitations\",    \"LibraryVariables\": \"/api/libraryvariablesets{/id}{?skip,contentType}\",    \"Lifecycles\": \"/api/lifecycles{/id}{?skip}\",    \"MachineRoles\": \"/api/machineroles/all\",    \"Machines\": \"/api/machines{/id}{?skip,thumbprint}\",    \"MaintenanceConfiguration\": \"/api/maintenanceconfiguration\",    \"OctopusServerNodes\": \"/api/octopusservernodes{/id}\",    \"Packages\": \"/api/packages{/id}{?nuGetPackageId,filter,latest,skip,take,includeNotes}\",    \"PackagesBulk\": \"/api/packages/bulk{?ids}\",    \"PackageUpload\": \"/api/packages/raw{?replace}\",    \"ProjectGroups\": \"/api/projectgroups{/id}{?skip}\",    \"Projects\": \"/api/projects{/id}{?skip,clone}\",    \"ProjectPulse\": \"/api/projects/pulse{?projectIds}\",    \"Register\": \"/api/users/register\",    \"Releases\": \"/api/releases{/id}{?skip,ignoreChannelRules}\",    \"ServerStatus\": \"/api/serverstatus\",    \"SignIn\": \"/api/users/login{?returnUrl}\",    \"SignOut\": \"/api/users/logout\",    \"RetentionPolicies\": \"/api/retentionpolicies{/id}{?skip}\",    \"SmtpConfiguration\": \"/api/smtpconfiguration\",    \"Tasks\": \"/api/tasks{/id}{?skip,active,environment,project,name,node,running}\",    \"Teams\": \"/api/teams{/id}{?skip}\",    \"UserRoles\": \"/api/userroles{/id}{?skip}\",    \"Users\": \"/api/users{/id}{?skip}\",    \"PermissionDescriptions\": \"/api/permissions/all\",    \"Variables\": \"/api/variables{/id}\",    \"VariableNames\": \"/api/variables/names{?project}\",    \"VersionRuleTest\": \"/api/channels/rule-test{?version,versionRange,preReleaseTag}\",    \"Reporting/DeploymentsCountedByWeek\": \"/api/reporting/deployments-counted-by-week{?projectIds}\",    \"RepositoryConfiguration\": \"/api/repository/configuration\",    \"Web\": \"/app\"  }}";
      }
      if (s.equals(octopusUrl + "/api/projects")) {
        return "{\"ItemType\": \"Project\",\"IsStale\": false,\"TotalResults\": 7,\"ItemsPerPage\": 30,\"Items\": [{\"Id\": \"Projects-24\",\"VariableSetId\": \"variableset-Projects-24\",\"DeploymentProcessId\": \"deploymentprocess-Projects-24\",\"IncludedLibraryVariableSetIds\": [],\"DefaultToSkipIfAlreadyInstalled\": false,\"VersioningStrategy\": {\"DonorPackageStepId\": null,\"Template\": \"#{Octopus.Version.LastMajor}.#{Octopus.Version.LastMinor}.#{Octopus.Version.NextPatch}\"},\"ReleaseCreationStrategy\": {\"ReleaseCreationPackageStepId\": null,\"ChannelId\": null},\"Name\": \"Project with latest deployment successful\",\"Slug\": \"project-with-latest-deployment-successful\",\"Description\": \"\",\"IsDisabled\": false,\"ProjectGroupId\": \"ProjectGroups-1\",\"LifecycleId\": \"Lifecycles-1\",\"AutoCreateRelease\": false,\"Links\": {\"Self\": \"/api/projects/Projects-24\",\"Releases\": \"/api/projects/Projects-24/releases{/version}{?skip}\",\"Channels\": \"/api/projects/Projects-24/channels\",\"OrderChannels\": \"/api/projects/Projects-24/channels/order\",\"Variables\": \"/api/variables/variableset-Projects-24\",\"Progression\": \"/api/progression/Projects-24\",\"DeploymentProcess\": \"/api/deploymentprocesses/deploymentprocess-Projects-24\",\"Web\": \"/app#/projects/Projects-24\",\"Logo\": \"/api/projects/Projects-24/logo\"}},{\"Id\": \"Projects-28\",\"VariableSetId\": \"variableset-Projects-28\",\"DeploymentProcessId\": \"deploymentprocess-Projects-28\",\"IncludedLibraryVariableSetIds\": [],\"DefaultToSkipIfAlreadyInstalled\": false,\"VersioningStrategy\": {\"DonorPackageStepId\": null,\"Template\": \"#{Octopus.Version.LastMajor}.#{Octopus.Version.LastMinor}.#{Octopus.Version.NextPatch}\"},\"ReleaseCreationStrategy\": {\"ReleaseCreationPackageStepId\": \"\",\"ChannelId\": null},\"Name\": \"Project with multiple environments\",\"Slug\": \"project-with-multiple-environments\",\"Description\": \"\",\"IsDisabled\": false,\"ProjectGroupId\": \"ProjectGroups-1\",\"LifecycleId\": \"Lifecycles-21\",\"AutoCreateRelease\": false,\"Links\": {\"Self\": \"/api/projects/Projects-28\",\"Releases\": \"/api/projects/Projects-28/releases{/version}{?skip}\",\"Channels\": \"/api/projects/Projects-28/channels\",\"OrderChannels\": \"/api/projects/Projects-28/channels/order\",\"Variables\": \"/api/variables/variableset-Projects-28\",\"Progression\": \"/api/progression/Projects-28\",\"DeploymentProcess\": \"/api/deploymentprocesses/deploymentprocess-Projects-28\",\"Web\": \"/app#/projects/Projects-28\",\"Logo\": \"/api/projects/Projects-28/logo\"}},{\"Id\": \"Projects-27\",\"VariableSetId\": \"variableset-Projects-27\",\"DeploymentProcessId\": \"deploymentprocess-Projects-27\",\"IncludedLibraryVariableSetIds\": [],\"DefaultToSkipIfAlreadyInstalled\": false,\"VersioningStrategy\": {\"DonorPackageStepId\": null,\"Template\": \"#{Octopus.Version.LastMajor}.#{Octopus.Version.LastMinor}.#{Octopus.Version.NextPatch}\"},\"ReleaseCreationStrategy\": {\"ReleaseCreationPackageStepId\": \"\",\"ChannelId\": null},\"Name\": \"Project with multiple environments and most recent deployment successful\",\"Slug\": \"project-with-multiple-environments-and-most-recent-deployment-successful\",\"Description\": \"\",\"IsDisabled\": false,\"ProjectGroupId\": \"ProjectGroups-1\",\"LifecycleId\": \"Lifecycles-21\",\"AutoCreateRelease\": false,\"Links\": {\"Self\": \"/api/projects/Projects-27\",\"Releases\": \"/api/projects/Projects-27/releases{/version}{?skip}\",\"Channels\": \"/api/projects/Projects-27/channels\",\"OrderChannels\": \"/api/projects/Projects-27/channels/order\",\"Variables\": \"/api/variables/variableset-Projects-27\",\"Progression\": \"/api/progression/Projects-27\",\"DeploymentProcess\": \"/api/deploymentprocesses/deploymentprocess-Projects-27\",\"Web\": \"/app#/projects/Projects-27\",\"Logo\": \"/api/projects/Projects-27/logo\"}},{\"Id\": \"Projects-23\",\"VariableSetId\": \"variableset-Projects-23\",\"DeploymentProcessId\": \"deploymentprocess-Projects-23\",\"IncludedLibraryVariableSetIds\": [],\"DefaultToSkipIfAlreadyInstalled\": false,\"VersioningStrategy\": {\"DonorPackageStepId\": null,\"Template\": \"#{Octopus.Version.LastMajor}.#{Octopus.Version.LastMinor}.#{Octopus.Version.NextPatch}\"},\"ReleaseCreationStrategy\": {\"ReleaseCreationPackageStepId\": null,\"ChannelId\": null},\"Name\": \"Project with no deployments\",\"Slug\": \"project-with-no-deployments\",\"Description\": \"\",\"IsDisabled\": false,\"ProjectGroupId\": \"ProjectGroups-1\",\"LifecycleId\": \"Lifecycles-1\",\"AutoCreateRelease\": false,\"Links\": {\"Self\": \"/api/projects/Projects-23\",\"Releases\": \"/api/projects/Projects-23/releases{/version}{?skip}\",\"Channels\": \"/api/projects/Projects-23/channels\",\"OrderChannels\": \"/api/projects/Projects-23/channels/order\",\"Variables\": \"/api/variables/variableset-Projects-23\",\"Progression\": \"/api/progression/Projects-23\",\"DeploymentProcess\": \"/api/deploymentprocesses/deploymentprocess-Projects-23\",\"Web\": \"/app#/projects/Projects-23\",\"Logo\": \"/api/projects/Projects-23/logo\"}},{\"Id\": \"Projects-26\",\"VariableSetId\": \"variableset-Projects-26\",\"DeploymentProcessId\": \"deploymentprocess-Projects-26\",\"IncludedLibraryVariableSetIds\": [],\"DefaultToSkipIfAlreadyInstalled\": false,\"VersioningStrategy\": {\"DonorPackageStepId\": null,\"Template\": \"#{Octopus.Version.LastMajor}.#{Octopus.Version.LastMinor}.#{Octopus.Version.NextPatch}\"},\"ReleaseCreationStrategy\": {\"ReleaseCreationPackageStepId\": null,\"ChannelId\": null},\"Name\": \"Project with no recent successful deployments\",\"Slug\": \"project-with-no-recent-successful-deployments\",\"Description\": \"\",\"IsDisabled\": false,\"ProjectGroupId\": \"ProjectGroups-1\",\"LifecycleId\": \"Lifecycles-1\",\"AutoCreateRelease\": false,\"Links\": {\"Self\": \"/api/projects/Projects-26\",\"Releases\": \"/api/projects/Projects-26/releases{/version}{?skip}\",\"Channels\": \"/api/projects/Projects-26/channels\",\"OrderChannels\": \"/api/projects/Projects-26/channels/order\",\"Variables\": \"/api/variables/variableset-Projects-26\",\"Progression\": \"/api/progression/Projects-26\",\"DeploymentProcess\": \"/api/deploymentprocesses/deploymentprocess-Projects-26\",\"Web\": \"/app#/projects/Projects-26\",\"Logo\": \"/api/projects/Projects-26/logo\"}},{\"Id\": \"Projects-22\",\"VariableSetId\": \"variableset-Projects-22\",\"DeploymentProcessId\": \"deploymentprocess-Projects-22\",\"IncludedLibraryVariableSetIds\": [],\"DefaultToSkipIfAlreadyInstalled\": false,\"VersioningStrategy\": {\"DonorPackageStepId\": null,\"Template\": \"#{Octopus.Version.LastMajor}.#{Octopus.Version.LastMinor}.#{Octopus.Version.NextPatch}\"},\"ReleaseCreationStrategy\": {\"ReleaseCreationPackageStepId\": null,\"ChannelId\": null},\"Name\": \"Project with no releases\",\"Slug\": \"project-with-no-releases\",\"Description\": \"\",\"IsDisabled\": false,\"ProjectGroupId\": \"ProjectGroups-1\",\"LifecycleId\": \"Lifecycles-1\",\"AutoCreateRelease\": false,\"Links\": {\"Self\": \"/api/projects/Projects-22\",\"Releases\": \"/api/projects/Projects-22/releases{/version}{?skip}\",\"Channels\": \"/api/projects/Projects-22/channels\",\"OrderChannels\": \"/api/projects/Projects-22/channels/order\",\"Variables\": \"/api/variables/variableset-Projects-22\",\"Progression\": \"/api/progression/Projects-22\",\"DeploymentProcess\": \"/api/deploymentprocesses/deploymentprocess-Projects-22\",\"Web\": \"/app#/projects/Projects-22\",\"Logo\": \"/api/projects/Projects-22/logo\"}},{\"Id\": \"Projects-25\",\"VariableSetId\": \"variableset-Projects-25\",\"DeploymentProcessId\": \"deploymentprocess-Projects-25\",\"IncludedLibraryVariableSetIds\": [],\"DefaultToSkipIfAlreadyInstalled\": false,\"VersioningStrategy\": {\"DonorPackageStepId\": null,\"Template\": \"#{Octopus.Version.LastMajor}.#{Octopus.Version.LastMinor}.#{Octopus.Version.NextPatch}\"},\"ReleaseCreationStrategy\": {\"ReleaseCreationPackageStepId\": null,\"ChannelId\": null},\"Name\": \"Project with no successful deployments\",\"Slug\": \"project-with-no-successful-deployments\",\"Description\": \"\",\"IsDisabled\": false,\"ProjectGroupId\": \"ProjectGroups-1\",\"LifecycleId\": \"Lifecycles-1\",\"AutoCreateRelease\": false,\"Links\": {\"Self\": \"/api/projects/Projects-25\",\"Releases\": \"/api/projects/Projects-25/releases{/version}{?skip}\",\"Channels\": \"/api/projects/Projects-25/channels\",\"OrderChannels\": \"/api/projects/Projects-25/channels/order\",\"Variables\": \"/api/variables/variableset-Projects-25\",\"Progression\": \"/api/progression/Projects-25\",\"DeploymentProcess\": \"/api/deploymentprocesses/deploymentprocess-Projects-25\",\"Web\": \"/app#/projects/Projects-25\",\"Logo\": \"/api/projects/Projects-25/logo\"}}],\"Links\": {\"Self\": \"/api/projects\",\"Template\": \"/api/projects{?skip}\",\"Page.Current\": \"/api/projects?skip=0\",\"Page.0\": \"/api/projects?skip=0\"}}";
      }
      if (s.equals(octopusUrl + "/api/progression/Projects-24")) {
        return "{\"Environments\": [{\"Id\": \"Environments-1\",\"Name\": \"local\"}],\"Releases\": [{\"Release\": {\"Id\": \"Releases-63\",\"Assembled\": \"2016-01-21T13:31:50.304+00:00\",\"ReleaseNotes\": null,\"ProjectId\": \"Projects-24\",\"ChannelId\": \"Channels-24\",\"ProjectVariableSetSnapshotId\": \"variableset-Projects-24-s-0-ZWV6Q\",\"LibraryVariableSetSnapshotIds\": [],\"ProjectDeploymentProcessSnapshotId\": \"deploymentprocess-Projects-24-s-0-F639H\",\"SelectedPackages\": [],\"Version\": \"0.0.1\",\"Links\": {\"Self\": \"/api/releases/Releases-63{?force}\",\"Project\": \"/api/projects/Projects-24\",\"Progression\": \"/api/releases/Releases-63/progression\",\"Deployments\": \"/api/releases/Releases-63/deployments\",\"DeploymentTemplate\": \"/api/releases/Releases-63/deployments/template\",\"Artifacts\": \"/api/artifacts?regarding=Releases-63\",\"ProjectVariableSnapshot\": \"/api/variables/variableset-Projects-24-s-0-ZWV6Q\",\"ProjectDeploymentProcessSnapshot\": \"/api/deploymentprocesses/deploymentprocess-Projects-24-s-0-F639H\",\"Web\": \"/app#/releases/Releases-63\",\"SnapshotVariables\": \"/api/releases/Releases-63/snapshot-variables\",\"Defects\": \"/api/releases/Releases-63/defects\",\"ReportDefect\": \"/api/releases/Releases-63/defects\",\"ResolveDefect\": \"/api/releases/Releases-63/defects/resolve\"}},\"Deployments\": {\"Environments-1\": {\"Id\": \"Deployments-81\",\"ProjectId\": \"Projects-24\",\"EnvironmentId\": \"Environments-1\",\"ReleaseId\": \"Releases-63\",\"DeploymentId\": \"Deployments-81\",\"TaskId\": \"ServerTasks-271\",\"ReleaseVersion\": \"0.0.1\",\"Created\": \"2016-01-21T13:31:56.022+00:00\",\"QueueTime\": \"2016-01-21T13:31:56.007+00:00\",\"CompletedTime\": \"2016-01-21T13:32:05.429+00:00\",\"State\": \"Success\",\"HasPendingInterruptions\": false,\"HasWarningsOrErrors\": false,\"ErrorMessage\": \"\",\"Duration\": \"9 seconds\",\"IsCurrent\": true,\"IsPrevious\": false,\"IsCompleted\": true,\"Links\": {\"Self\": \"/api/deployments/Deployments-81\",\"Release\": \"/api/releases/Releases-63\",\"Task\": \"/api/tasks/ServerTasks-271\"}}},\"NextDeployments\": [],\"HasUnresolvedDefect\": false,\"ReleaseRetentionPeriod\": null,\"TentacleRetentionPeriod\": null}],\"Links\": {}}";
      }
      if (s.equals(octopusUrl + "/api/progression/Projects-28")) {
        return "{\"Environments\": [{\"Id\": \"Environments-1\",\"Name\": \"local\"},{\"Id\": \"Environments-21\",\"Name\": \"Prod\"}],\"Releases\": [{\"Release\": {\"Id\": \"Releases-70\",\"Assembled\": \"2016-01-21T14:26:10.732+00:00\",\"ReleaseNotes\": null,\"ProjectId\": \"Projects-28\",\"ChannelId\": \"Channels-28\",\"ProjectVariableSetSnapshotId\": \"variableset-Projects-28-s-0-4NHSR\",\"LibraryVariableSetSnapshotIds\": [],\"ProjectDeploymentProcessSnapshotId\": \"deploymentprocess-Projects-28-s-1-N56TF\",\"SelectedPackages\": [],\"Version\": \"0.0.2\",\"Links\": {\"Self\": \"/api/releases/Releases-70{?force}\",\"Project\": \"/api/projects/Projects-28\",\"Progression\": \"/api/releases/Releases-70/progression\",\"Deployments\": \"/api/releases/Releases-70/deployments\",\"DeploymentTemplate\": \"/api/releases/Releases-70/deployments/template\",\"Artifacts\": \"/api/artifacts?regarding=Releases-70\",\"ProjectVariableSnapshot\": \"/api/variables/variableset-Projects-28-s-0-4NHSR\",\"ProjectDeploymentProcessSnapshot\": \"/api/deploymentprocesses/deploymentprocess-Projects-28-s-1-N56TF\",\"Web\": \"/app#/releases/Releases-70\",\"SnapshotVariables\": \"/api/releases/Releases-70/snapshot-variables\",\"Defects\": \"/api/releases/Releases-70/defects\",\"ReportDefect\": \"/api/releases/Releases-70/defects\",\"ResolveDefect\": \"/api/releases/Releases-70/defects/resolve\"}},\"Deployments\": {\"Environments-1\": {\"Id\": \"Deployments-119\",\"ProjectId\": \"Projects-28\",\"EnvironmentId\": \"Environments-1\",\"ReleaseId\": \"Releases-70\",\"DeploymentId\": \"Deployments-119\",\"TaskId\": \"ServerTasks-310\",\"ReleaseVersion\": \"0.0.2\",\"Created\": \"2016-01-21T14:26:14.747+00:00\",\"QueueTime\": \"2016-01-21T14:26:14.747+00:00\",\"CompletedTime\": \"2016-01-21T14:26:19.200+00:00\",\"State\": \"Failed\",\"HasPendingInterruptions\": false,\"HasWarningsOrErrors\": true,\"ErrorMessage\": \"The deployment failed because one or more steps failed. Please see the deployment log for details.\",\"Duration\": \"4 seconds\",\"IsCurrent\": true,\"IsPrevious\": false,\"IsCompleted\": true,\"Links\": {\"Self\": \"/api/deployments/Deployments-119\",\"Release\": \"/api/releases/Releases-70\",\"Task\": \"/api/tasks/ServerTasks-310\"}}},\"NextDeployments\": [\"Environments-1\"],\"HasUnresolvedDefect\": false,\"ReleaseRetentionPeriod\": null,\"TentacleRetentionPeriod\": null},{\"Release\": {\"Id\": \"Releases-69\",\"Assembled\": \"2016-01-21T14:25:36.888+00:00\",\"ReleaseNotes\": null,\"ProjectId\": \"Projects-28\",\"ChannelId\": \"Channels-28\",\"ProjectVariableSetSnapshotId\": \"variableset-Projects-28-s-0-4NHSR\",\"LibraryVariableSetSnapshotIds\": [],\"ProjectDeploymentProcessSnapshotId\": \"deploymentprocess-Projects-28-s-0-53ULV\",\"SelectedPackages\": [],\"Version\": \"0.0.1\",\"Links\": {\"Self\": \"/api/releases/Releases-69{?force}\",\"Project\": \"/api/projects/Projects-28\",\"Progression\": \"/api/releases/Releases-69/progression\",\"Deployments\": \"/api/releases/Releases-69/deployments\",\"DeploymentTemplate\": \"/api/releases/Releases-69/deployments/template\",\"Artifacts\": \"/api/artifacts?regarding=Releases-69\",\"ProjectVariableSnapshot\": \"/api/variables/variableset-Projects-28-s-0-4NHSR\",\"ProjectDeploymentProcessSnapshot\": \"/api/deploymentprocesses/deploymentprocess-Projects-28-s-0-53ULV\",\"Web\": \"/app#/releases/Releases-69\",\"SnapshotVariables\": \"/api/releases/Releases-69/snapshot-variables\",\"Defects\": \"/api/releases/Releases-69/defects\",\"ReportDefect\": \"/api/releases/Releases-69/defects\",\"ResolveDefect\": \"/api/releases/Releases-69/defects/resolve\"}},\"Deployments\": {\"Environments-1\": {\"Id\": \"Deployments-117\",\"ProjectId\": \"Projects-28\",\"EnvironmentId\": \"Environments-1\",\"ReleaseId\": \"Releases-69\",\"DeploymentId\": \"Deployments-117\",\"TaskId\": \"ServerTasks-308\",\"ReleaseVersion\": \"0.0.1\",\"Created\": \"2016-01-21T14:25:40.247+00:00\",\"QueueTime\": \"2016-01-21T14:25:40.247+00:00\",\"CompletedTime\": \"2016-01-21T14:25:44.779+00:00\",\"State\": \"Success\",\"HasPendingInterruptions\": false,\"HasWarningsOrErrors\": false,\"ErrorMessage\": \"\",\"Duration\": \"5 seconds\",\"IsCurrent\": false,\"IsPrevious\": true,\"IsCompleted\": true,\"Links\": {\"Self\": \"/api/deployments/Deployments-117\",\"Release\": \"/api/releases/Releases-69\",\"Task\": \"/api/tasks/ServerTasks-308\"}},\"Environments-21\": {\"Id\": \"Deployments-118\",\"ProjectId\": \"Projects-28\",\"EnvironmentId\": \"Environments-21\",\"ReleaseId\": \"Releases-69\",\"DeploymentId\": \"Deployments-118\",\"TaskId\": \"ServerTasks-309\",\"ReleaseVersion\": \"0.0.1\",\"Created\": \"2016-01-21T14:25:53.700+00:00\",\"QueueTime\": \"2016-01-21T14:25:53.700+00:00\",\"CompletedTime\": \"2016-01-21T14:25:57.373+00:00\",\"State\": \"Success\",\"HasPendingInterruptions\": false,\"HasWarningsOrErrors\": false,\"ErrorMessage\": \"\",\"Duration\": \"4 seconds\",\"IsCurrent\": true,\"IsPrevious\": false,\"IsCompleted\": true,\"Links\": {\"Self\": \"/api/deployments/Deployments-118\",\"Release\": \"/api/releases/Releases-69\",\"Task\": \"/api/tasks/ServerTasks-309\"}}},\"NextDeployments\": [],\"HasUnresolvedDefect\": false,\"ReleaseRetentionPeriod\": null,\"TentacleRetentionPeriod\": null}],\"Links\": {}}";
      }
      if (s.equals(octopusUrl + "/api/progression/Projects-27")) {
        return "{\"Environments\": [{\"Id\": \"Environments-1\",\"Name\": \"local\"},{\"Id\": \"Environments-21\",\"Name\": \"Prod\"}],\"Releases\": [{\"Release\": {\"Id\": \"Releases-68\",\"Assembled\": \"2016-01-21T14:24:27.138+00:00\",\"ReleaseNotes\": null,\"ProjectId\": \"Projects-27\",\"ChannelId\": \"Channels-27\",\"ProjectVariableSetSnapshotId\": \"variableset-Projects-27-s-0-VT8T2\",\"LibraryVariableSetSnapshotIds\": [],\"ProjectDeploymentProcessSnapshotId\": \"deploymentprocess-Projects-27-s-0-WNCQ3\",\"SelectedPackages\": [],\"Version\": \"0.0.2\",\"Links\": {\"Self\": \"/api/releases/Releases-68{?force}\",\"Project\": \"/api/projects/Projects-27\",\"Progression\": \"/api/releases/Releases-68/progression\",\"Deployments\": \"/api/releases/Releases-68/deployments\",\"DeploymentTemplate\": \"/api/releases/Releases-68/deployments/template\",\"Artifacts\": \"/api/artifacts?regarding=Releases-68\",\"ProjectVariableSnapshot\": \"/api/variables/variableset-Projects-27-s-0-VT8T2\",\"ProjectDeploymentProcessSnapshot\": \"/api/deploymentprocesses/deploymentprocess-Projects-27-s-0-WNCQ3\",\"Web\": \"/app#/releases/Releases-68\",\"SnapshotVariables\": \"/api/releases/Releases-68/snapshot-variables\",\"Defects\": \"/api/releases/Releases-68/defects\",\"ReportDefect\": \"/api/releases/Releases-68/defects\",\"ResolveDefect\": \"/api/releases/Releases-68/defects/resolve\"}},\"Deployments\": {\"Environments-1\": {\"Id\": \"Deployments-116\",\"ProjectId\": \"Projects-27\",\"EnvironmentId\": \"Environments-1\",\"ReleaseId\": \"Releases-68\",\"DeploymentId\": \"Deployments-116\",\"TaskId\": \"ServerTasks-307\",\"ReleaseVersion\": \"0.0.2\",\"Created\": \"2016-01-21T14:24:30.935+00:00\",\"QueueTime\": \"2016-01-21T14:24:30.935+00:00\",\"CompletedTime\": \"2016-01-21T14:24:34.607+00:00\",\"State\": \"Success\",\"HasPendingInterruptions\": false,\"HasWarningsOrErrors\": false,\"ErrorMessage\": \"\",\"Duration\": \"4 seconds\",\"IsCurrent\": true,\"IsPrevious\": false,\"IsCompleted\": true,\"Links\": {\"Self\": \"/api/deployments/Deployments-116\",\"Release\": \"/api/releases/Releases-68\",\"Task\": \"/api/tasks/ServerTasks-307\"}}},\"NextDeployments\": [\"Environments-21\"],\"HasUnresolvedDefect\": false,\"ReleaseRetentionPeriod\": null,\"TentacleRetentionPeriod\": null},{\"Release\": {\"Id\": \"Releases-67\",\"Assembled\": \"2016-01-21T14:23:59.185+00:00\",\"ReleaseNotes\": null,\"ProjectId\": \"Projects-27\",\"ChannelId\": \"Channels-27\",\"ProjectVariableSetSnapshotId\": \"variableset-Projects-27-s-0-VT8T2\",\"LibraryVariableSetSnapshotIds\": [],\"ProjectDeploymentProcessSnapshotId\": \"deploymentprocess-Projects-27-s-0-WNCQ3\",\"SelectedPackages\": [],\"Version\": \"0.0.1\",\"Links\": {\"Self\": \"/api/releases/Releases-67{?force}\",\"Project\": \"/api/projects/Projects-27\",\"Progression\": \"/api/releases/Releases-67/progression\",\"Deployments\": \"/api/releases/Releases-67/deployments\",\"DeploymentTemplate\": \"/api/releases/Releases-67/deployments/template\",\"Artifacts\": \"/api/artifacts?regarding=Releases-67\",\"ProjectVariableSnapshot\": \"/api/variables/variableset-Projects-27-s-0-VT8T2\",\"ProjectDeploymentProcessSnapshot\": \"/api/deploymentprocesses/deploymentprocess-Projects-27-s-0-WNCQ3\",\"Web\": \"/app#/releases/Releases-67\",\"SnapshotVariables\": \"/api/releases/Releases-67/snapshot-variables\",\"Defects\": \"/api/releases/Releases-67/defects\",\"ReportDefect\": \"/api/releases/Releases-67/defects\",\"ResolveDefect\": \"/api/releases/Releases-67/defects/resolve\"}},\"Deployments\": {\"Environments-1\": {\"Id\": \"Deployments-114\",\"ProjectId\": \"Projects-27\",\"EnvironmentId\": \"Environments-1\",\"ReleaseId\": \"Releases-67\",\"DeploymentId\": \"Deployments-114\",\"TaskId\": \"ServerTasks-305\",\"ReleaseVersion\": \"0.0.1\",\"Created\": \"2016-01-21T14:24:02.685+00:00\",\"QueueTime\": \"2016-01-21T14:24:02.685+00:00\",\"CompletedTime\": \"2016-01-21T14:24:06.294+00:00\",\"State\": \"Success\",\"HasPendingInterruptions\": false,\"HasWarningsOrErrors\": false,\"ErrorMessage\": \"\",\"Duration\": \"4 seconds\",\"IsCurrent\": false,\"IsPrevious\": true,\"IsCompleted\": true,\"Links\": {\"Self\": \"/api/deployments/Deployments-114\",\"Release\": \"/api/releases/Releases-67\",\"Task\": \"/api/tasks/ServerTasks-305\"}},\"Environments-21\": {\"Id\": \"Deployments-115\",\"ProjectId\": \"Projects-27\",\"EnvironmentId\": \"Environments-21\",\"ReleaseId\": \"Releases-67\",\"DeploymentId\": \"Deployments-115\",\"TaskId\": \"ServerTasks-306\",\"ReleaseVersion\": \"0.0.1\",\"Created\": \"2016-01-21T14:24:10.872+00:00\",\"QueueTime\": \"2016-01-21T14:24:10.872+00:00\",\"CompletedTime\": \"2016-01-21T14:24:14.513+00:00\",\"State\": \"Success\",\"HasPendingInterruptions\": false,\"HasWarningsOrErrors\": false,\"ErrorMessage\": \"\",\"Duration\": \"4 seconds\",\"IsCurrent\": true,\"IsPrevious\": false,\"IsCompleted\": true,\"Links\": {\"Self\": \"/api/deployments/Deployments-115\",\"Release\": \"/api/releases/Releases-67\",\"Task\": \"/api/tasks/ServerTasks-306\"}}},\"NextDeployments\": [],\"HasUnresolvedDefect\": false,\"ReleaseRetentionPeriod\": null,\"TentacleRetentionPeriod\": null}],\"Links\": {}}";
      }
      if (s.equals(octopusUrl + "/api/progression/Projects-23")) {
        return "{\"Environments\": [{\"Id\": \"Environments-1\",\"Name\": \"local\"}],\"Releases\": [{\"Release\": {\"Id\": \"Releases-62\",\"Assembled\": \"2016-01-21T13:31:05.335+00:00\",\"ReleaseNotes\": null,\"ProjectId\": \"Projects-23\",\"ChannelId\": \"Channels-23\",\"ProjectVariableSetSnapshotId\": \"variableset-Projects-23-s-0-8ZM67\",\"LibraryVariableSetSnapshotIds\": [],\"ProjectDeploymentProcessSnapshotId\": \"deploymentprocess-Projects-23-s-0-LJS8Q\",\"SelectedPackages\": [],\"Version\": \"0.0.1\",\"Links\": {\"Self\": \"/api/releases/Releases-62{?force}\",\"Project\": \"/api/projects/Projects-23\",\"Progression\": \"/api/releases/Releases-62/progression\",\"Deployments\": \"/api/releases/Releases-62/deployments\",\"DeploymentTemplate\": \"/api/releases/Releases-62/deployments/template\",\"Artifacts\": \"/api/artifacts?regarding=Releases-62\",\"ProjectVariableSnapshot\": \"/api/variables/variableset-Projects-23-s-0-8ZM67\",\"ProjectDeploymentProcessSnapshot\": \"/api/deploymentprocesses/deploymentprocess-Projects-23-s-0-LJS8Q\",\"Web\": \"/app#/releases/Releases-62\",\"SnapshotVariables\": \"/api/releases/Releases-62/snapshot-variables\",\"Defects\": \"/api/releases/Releases-62/defects\",\"ReportDefect\": \"/api/releases/Releases-62/defects\",\"ResolveDefect\": \"/api/releases/Releases-62/defects/resolve\"}},\"Deployments\": {},\"NextDeployments\": [\"Environments-1\"],\"HasUnresolvedDefect\": false,\"ReleaseRetentionPeriod\": null,\"TentacleRetentionPeriod\": null}],\"Links\": {}}";
      }
      if (s.equals(octopusUrl + "/api/progression/Projects-26")) {
        return "{\"Environments\": [{\"Id\": \"Environments-1\",\"Name\": \"local\"}],\"Releases\": [{\"Release\": {\"Id\": \"Releases-66\",\"Assembled\": \"2016-01-21T13:37:02.882+00:00\",\"ReleaseNotes\": null,\"ProjectId\": \"Projects-26\",\"ChannelId\": \"Channels-26\",\"ProjectVariableSetSnapshotId\": \"variableset-Projects-26-s-0-56V4D\",\"LibraryVariableSetSnapshotIds\": [],\"ProjectDeploymentProcessSnapshotId\": \"deploymentprocess-Projects-26-s-2-4WY9H\",\"SelectedPackages\": [],\"Version\": \"0.0.2\",\"Links\": {\"Self\": \"/api/releases/Releases-66{?force}\",\"Project\": \"/api/projects/Projects-26\",\"Progression\": \"/api/releases/Releases-66/progression\",\"Deployments\": \"/api/releases/Releases-66/deployments\",\"DeploymentTemplate\": \"/api/releases/Releases-66/deployments/template\",\"Artifacts\": \"/api/artifacts?regarding=Releases-66\",\"ProjectVariableSnapshot\": \"/api/variables/variableset-Projects-26-s-0-56V4D\",\"ProjectDeploymentProcessSnapshot\": \"/api/deploymentprocesses/deploymentprocess-Projects-26-s-2-4WY9H\",\"Web\": \"/app#/releases/Releases-66\",\"SnapshotVariables\": \"/api/releases/Releases-66/snapshot-variables\",\"Defects\": \"/api/releases/Releases-66/defects\",\"ReportDefect\": \"/api/releases/Releases-66/defects\",\"ResolveDefect\": \"/api/releases/Releases-66/defects/resolve\"}},\"Deployments\": {\"Environments-1\": {\"Id\": \"Deployments-113\",\"ProjectId\": \"Projects-26\",\"EnvironmentId\": \"Environments-1\",\"ReleaseId\": \"Releases-66\",\"DeploymentId\": \"Deployments-113\",\"TaskId\": \"ServerTasks-303\",\"ReleaseVersion\": \"0.0.2\",\"Created\": \"2016-01-21T14:18:01.887+00:00\",\"QueueTime\": \"2016-01-21T14:18:01.887+00:00\",\"CompletedTime\": \"2016-01-21T14:18:06.294+00:00\",\"State\": \"Failed\",\"HasPendingInterruptions\": false,\"HasWarningsOrErrors\": true,\"ErrorMessage\": \"The deployment failed because one or more steps failed. Please see the deployment log for details.\",\"Duration\": \"4 seconds\",\"IsCurrent\": true,\"IsPrevious\": false,\"IsCompleted\": true,\"Links\": {\"Self\": \"/api/deployments/Deployments-113\",\"Release\": \"/api/releases/Releases-66\",\"Task\": \"/api/tasks/ServerTasks-303\"}}},\"NextDeployments\": [\"Environments-1\"],\"HasUnresolvedDefect\": false,\"ReleaseRetentionPeriod\": null,\"TentacleRetentionPeriod\": null}, {\"Release\": {\"Id\": \"Releases-65\",\"Assembled\": \"2016-01-21T13:35:13.023+00:00\",\"ReleaseNotes\": null,\"ProjectId\": \"Projects-26\",\"ChannelId\": \"Channels-26\",\"ProjectVariableSetSnapshotId\": \"variableset-Projects-26-s-0-56V4D\",\"LibraryVariableSetSnapshotIds\": [],\"ProjectDeploymentProcessSnapshotId\": \"deploymentprocess-Projects-26-s-1-JW52D\",\"SelectedPackages\": [],\"Version\": \"0.0.1\",\"Links\": {\"Self\": \"/api/releases/Releases-65{?force}\",\"Project\": \"/api/projects/Projects-26\",\"Progression\": \"/api/releases/Releases-65/progression\",\"Deployments\": \"/api/releases/Releases-65/deployments\",\"DeploymentTemplate\": \"/api/releases/Releases-65/deployments/template\",\"Artifacts\": \"/api/artifacts?regarding=Releases-65\",\"ProjectVariableSnapshot\": \"/api/variables/variableset-Projects-26-s-0-56V4D\",\"ProjectDeploymentProcessSnapshot\": \"/api/deploymentprocesses/deploymentprocess-Projects-26-s-1-JW52D\",\"Web\": \"/app#/releases/Releases-65\",\"SnapshotVariables\": \"/api/releases/Releases-65/snapshot-variables\",\"Defects\": \"/api/releases/Releases-65/defects\",\"ReportDefect\": \"/api/releases/Releases-65/defects\",\"ResolveDefect\": \"/api/releases/Releases-65/defects/resolve\"}},\"Deployments\": {\"Environments-1\": {\"Id\": \"Deployments-83\",\"ProjectId\": \"Projects-26\",\"EnvironmentId\": \"Environments-1\",\"ReleaseId\": \"Releases-65\",\"DeploymentId\": \"Deployments-83\",\"TaskId\": \"ServerTasks-273\",\"ReleaseVersion\": \"0.0.1\",\"Created\": \"2016-01-21T13:35:27.179+00:00\",\"QueueTime\": \"2016-01-21T13:35:27.179+00:00\",\"CompletedTime\": \"2016-01-21T13:36:13.086+00:00\",\"State\": \"Success\",\"HasPendingInterruptions\": false,\"HasWarningsOrErrors\": false,\"ErrorMessage\": \"\",\"Duration\": \"46 seconds\",\"IsCurrent\": false,\"IsPrevious\": false,\"IsCompleted\": true,\"Links\": {\"Self\": \"/api/deployments/Deployments-83\",\"Release\": \"/api/releases/Releases-65\",\"Task\": \"/api/tasks/ServerTasks-273\"}}},\"NextDeployments\": [],\"HasUnresolvedDefect\": false,\"ReleaseRetentionPeriod\": null,\"TentacleRetentionPeriod\": null}],\"Links\": {}}";
      }
      if (s.equals(octopusUrl + "/api/progression/Projects-22")) {
        return "{\"Environments\": [{\"Id\": \"Environments-1\",\"Name\": \"local\"}],\"Releases\": [],\"Links\": {}}";
      }
      if (s.equals(octopusUrl + "/api/progression/Projects-25")) {
        return "{\"Environments\": [{\"Id\": \"Environments-1\",\"Name\": \"local\"}],\"Releases\": [{\"Release\": {\"Id\": \"Releases-64\",\"Assembled\": \"2016-01-21T13:32:56.382+00:00\",\"ReleaseNotes\": null,\"ProjectId\": \"Projects-25\",\"ChannelId\": \"Channels-25\",\"ProjectVariableSetSnapshotId\": \"variableset-Projects-25-s-0-CF4HB\",\"LibraryVariableSetSnapshotIds\": [],\"ProjectDeploymentProcessSnapshotId\": \"deploymentprocess-Projects-25-s-1-5JFQU\",\"SelectedPackages\": [],\"Version\": \"0.0.1\",\"Links\": {\"Self\": \"/api/releases/Releases-64{?force}\",\"Project\": \"/api/projects/Projects-25\",\"Progression\": \"/api/releases/Releases-64/progression\",\"Deployments\": \"/api/releases/Releases-64/deployments\",\"DeploymentTemplate\": \"/api/releases/Releases-64/deployments/template\",\"Artifacts\": \"/api/artifacts?regarding=Releases-64\",\"ProjectVariableSnapshot\": \"/api/variables/variableset-Projects-25-s-0-CF4HB\",\"ProjectDeploymentProcessSnapshot\": \"/api/deploymentprocesses/deploymentprocess-Projects-25-s-1-5JFQU\",\"Web\": \"/app#/releases/Releases-64\",\"SnapshotVariables\": \"/api/releases/Releases-64/snapshot-variables\",\"Defects\": \"/api/releases/Releases-64/defects\",\"ReportDefect\": \"/api/releases/Releases-64/defects\",\"ResolveDefect\": \"/api/releases/Releases-64/defects/resolve\"}},\"Deployments\": {\"Environments-1\": {\"Id\": \"Deployments-82\",\"ProjectId\": \"Projects-25\",\"EnvironmentId\": \"Environments-1\",\"ReleaseId\": \"Releases-64\",\"DeploymentId\": \"Deployments-82\",\"TaskId\": \"ServerTasks-272\",\"ReleaseVersion\": \"0.0.1\",\"Created\": \"2016-01-21T13:32:59.991+00:00\",\"QueueTime\": \"2016-01-21T13:32:59.976+00:00\",\"CompletedTime\": \"2016-01-21T13:33:39.571+00:00\",\"State\": \"Failed\",\"HasPendingInterruptions\": false,\"HasWarningsOrErrors\": true,\"ErrorMessage\": \"The deployment failed because one or more steps failed. Please see the deployment log for details.\",\"Duration\": \"40 seconds\",\"IsCurrent\": true,\"IsPrevious\": false,\"IsCompleted\": true,\"Links\": {\"Self\": \"/api/deployments/Deployments-82\",\"Release\": \"/api/releases/Releases-64\",\"Task\": \"/api/tasks/ServerTasks-272\"}}},\"NextDeployments\": [\"Environments-1\"],\"HasUnresolvedDefect\": false,\"ReleaseRetentionPeriod\": null,\"TentacleRetentionPeriod\": null}],\"Links\": {}}";
      }
      if (s.equals(octopusUrl + "/api/deployments?Projects=Projects-25")) {
        return "{\"ItemType\": \"Deployment\",\"IsStale\": false,\"TotalResults\": 1,\"ItemsPerPage\": 30,\"Items\": [{\"Id\": \"Deployments-82\",\"ReleaseId\": \"Releases-64\",\"EnvironmentId\": \"Environments-1\",\"ForcePackageDownload\": false,\"ForcePackageRedeployment\": false,\"SkipActions\": [],\"SpecificMachineIds\": [],\"DeploymentProcessId\": \"deploymentprocess-Projects-25-s-1-5JFQU\",\"ManifestVariableSetId\": \"variableset-Deployments-82\",\"TaskId\": \"ServerTasks-272\",\"ProjectId\": \"Projects-25\",\"UseGuidedFailure\": true,\"Comments\": null,\"FormValues\": {},\"QueueTime\": null,\"Name\": \"Deploy to local\",\"Created\": \"2016-01-21T13:32:59.991+00:00\",\"Links\": {\"Self\": \"/api/deployments/Deployments-82\",\"Release\": \"/api/releases/Releases-64\",\"Environment\": \"/api/environments/Environments-1\",\"Project\": \"/api/projects/Projects-25\",\"Task\": \"/api/tasks/ServerTasks-272\",\"Web\": \"/app#/deployments/Deployments-82\",\"Artifacts\": \"/api/artifacts?regarding=Deployments-82\",\"Variables\": \"/api/variables/variableset-Deployments-82\",\"Interruptions\": \"/api/interruptions?regarding=Deployments-82\"}}],\"Links\": {\"Self\": \"/api/deployments?projects=Projects-25\",\"Template\": \"/api/deployments{?skip,take,projects,environments,taskState}\",\"Page.Current\": \"/api/deployments?skip=0&projects=Projects-25\",\"Page.0\": \"/api/deployments?skip=0&projects=Projects-25\"}}";
      }
      if (s.equals(octopusUrl + "/api/tasks/ServerTasks-272")) {
        return "{\"Id\": \"ServerTasks-272\",\"Name\": \"Deploy\",\"Description\": \"Deploy Project with no successful deployments release 0.0.1 to local\",\"Arguments\": {\"DeploymentId\": \"Deployments-82\"},\"State\": \"Failed\",\"Completed\": \"21 January 2016 13:33\",\"QueueTime\": \"2016-01-21T13:32:59.976+00:00\",\"StartTime\": \"2016-01-21T13:33:00.038+00:00\",\"LastUpdatedTime\": \"2016-01-21T13:33:39.571+00:00\",\"CompletedTime\": \"2016-01-21T13:33:39.571+00:00\",\"ServerNode\": \"DESKTOP-4VQNCHP\",\"Duration\": \"40 seconds\",\"ErrorMessage\": \"The deployment failed because one or more steps failed. Please see the deployment log for details.\",\"HasBeenPickedUpByProcessor\": true,\"IsCompleted\": true,\"FinishedSuccessfully\": false,\"HasPendingInterruptions\": false,\"CanRerun\": false,\"HasWarningsOrErrors\": true,\"Links\": {\"Self\": \"/api/tasks/ServerTasks-272\",\"Web\": \"/app#/tasks/ServerTasks-272\",\"Raw\": \"/api/tasks/ServerTasks-272/raw\",\"Rerun\": \"/api/tasks/rerun/ServerTasks-272\",\"Cancel\": \"/api/tasks/ServerTasks-272/cancel\",\"QueuedBehind\": \"/api/tasks/ServerTasks-272/queued-behind\",\"Details\": \"/api/tasks/ServerTasks-272/details{?verbose,tail}\",\"Artifacts\": \"/api/artifacts?regarding=ServerTasks-272\",\"Interruptions\": \"/api/tasks\"}}";
      }
      throw new InvalidOctopusUrlException(new URI(s));
    }

    public String getUrl() {
      return null;
    }
  }
}
