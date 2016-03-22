package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.NullOctopusDate;
import com.mjrichardson.teamCity.buildTriggers.OctopusDate;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;


@Test
public class EnvironmentTest {
    public void isLatestDeploymentOlderThanReturnsTrueWhenNewerDatePassed() {
        OctopusDate testDate = new OctopusDate(2015, 12, 10);
        Environment environment = new Environment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 9));
        Assert.assertEquals(environment.isLatestDeploymentOlderThan(testDate), true);
    }

    public void isLatestDeploymentOlderThanReturnsFalseWhenOlderDatePassed() {
        OctopusDate testDate = new OctopusDate(2015, 12, 8);
        Environment environment = new Environment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 9));
        Assert.assertEquals(environment.isLatestDeploymentOlderThan(testDate), false);
    }

    public void isLatestDeploymentOlderThanReturnsFalseWhenSameDatePassed() {
        OctopusDate testDate = new OctopusDate(2015, 12, 9);
        Environment environment = new Environment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 9));
        Assert.assertEquals(environment.isLatestDeploymentOlderThan(testDate), false);
    }

    public void isLatestDeploymentOlderThanComparesAgainstLatestDeploymentDate() {
        OctopusDate testDate = new OctopusDate(2015, 12, 10);
        Environment environment = new Environment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 12));
        Assert.assertEquals(environment.isLatestDeploymentOlderThan(testDate), true);
    }

    public void isLatestSuccessfulDeploymentOlderThenReturnsTrueWhenNewerDatePassed() {
        //new OctopusDate(2014, Calendar.FEBRUARY, 11).getTime(
        OctopusDate testDate = new OctopusDate(2015, 12, 10);
        Environment environment = new Environment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 9));
        Assert.assertEquals(environment.isLatestDeploymentOlderThan(testDate), true);
    }

    public void isLatestSuccessfulDeploymentOlderThenReturnsFalseWhenOlderDatePassed() {
        OctopusDate testDate = new OctopusDate(2015, 12, 8);
        Environment environment = new Environment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 9));
        Assert.assertEquals(environment.isLatestDeploymentOlderThan(testDate), false);
    }

    public void isLatestSuccessfulDeploymentOlderThenReturnsFalseWhenSameDatePassed() {
        OctopusDate testDate = new OctopusDate(2015, 12, 9);
        Environment environment = new Environment("env", new OctopusDate(2015, 12, 9), new OctopusDate(2015, 12, 9));
        Assert.assertEquals(environment.isLatestDeploymentOlderThan(testDate), false);
    }

    public void isLatestSuccessfulDeploymentOlderThenComparesAgainstLatestDeploymentDate() {
        OctopusDate testDate = new OctopusDate(2015, 12, 10);
        Environment environment = new Environment("env", new OctopusDate(2015, 12, 12), new OctopusDate(2015, 12, 9));
        Assert.assertEquals(environment.isLatestSuccessfulDeploymentOlderThen(testDate), true);
    }

    public void toStringFormatsCorrectly() {
        Environment environment = new Environment("env", new OctopusDate(2015, 12, 9, 14, 10, 11), new OctopusDate(2015, 12, 12, 9, 4, 3));
        Assert.assertEquals(environment.toString(), "env;2015-12-09T14:10:11.000+00:00;2015-12-12T09:04:03.000+00:00");
    }

    public void parse_returns_null_environment_if_is_not_completed() {
        HashMap<String, String> map = new HashMap<>();
        map.put("EnvironmentId", "Environment-1");
        map.put("Created", "2016-02-26T17:58:13.537+00:00");
        map.put("IsCompleted", "false");
        map.put("State", "Unknown");
        Environment sut = Environment.Parse(map);
        Assert.assertEquals(sut.getClass(), NullEnvironment.class);
    }

    public void parse_returns_deployment_with_valid_latest_successful_deployment_if_successful() {
        HashMap<String, String> map = new HashMap<>();
        map.put("EnvironmentId", "Environment-1");
        map.put("Created", "2016-02-26T17:58:13.537+00:00");
        map.put("IsCompleted", "true");
        map.put("State", "Success");
        map.put("ReleaseId", "Release-1");
        map.put("DeploymentId", "Deployment-1");
        map.put("ReleaseVersion", "1.0.1");
        map.put("ProjectId", "Project-8");
        Environment sut = Environment.Parse(map);
        Assert.assertEquals(sut.environmentId, "Environment-1");
        Assert.assertEquals(sut.latestDeployment, new OctopusDate(2016, 2, 26, 17, 58, 13, 537));
        Assert.assertEquals(sut.latestSuccessfulDeployment, new OctopusDate(2016, 2, 26, 17, 58, 13, 537));
        Assert.assertEquals(sut.releaseId, "Release-1");
        Assert.assertEquals(sut.deploymentId, "Deployment-1");
        Assert.assertEquals(sut.version, "1.0.1");
        Assert.assertEquals(sut.projectId, "Project-8");
    }

    public void parse_returns_deployment_with_null_latest_successful_deployment_if_not_successful() {
        HashMap<String, String> map = new HashMap<>();
        map.put("EnvironmentId", "Environment-1");
        map.put("Created", "2016-02-26T17:58:13.537+00:00");
        map.put("IsCompleted", "true");
        map.put("State", "Failed");
        map.put("ReleaseId", "Release-1");
        map.put("DeploymentId", "Deployment-1");
        map.put("ReleaseVersion", "1.0.1");
        map.put("ProjectId", "Project-8");
        Environment sut = Environment.Parse(map);
        Assert.assertEquals(sut.environmentId, "Environment-1");
        Assert.assertEquals(sut.latestDeployment, new OctopusDate(2016, 2, 26, 17, 58, 13, 537));
        Assert.assertEquals(sut.latestSuccessfulDeployment.getClass(), NullOctopusDate.class);
        Assert.assertEquals(sut.releaseId, "Release-1");
        Assert.assertEquals(sut.deploymentId, "Deployment-1");
        Assert.assertEquals(sut.version, "1.0.1");
        Assert.assertEquals(sut.projectId, "Project-8");
    }

    public void is_latest_successful_deployment_newer_than_returns_false_if_passed_date_is_newer() {
        Environment sut = new Environment("environment-1", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 26));
        Assert.assertFalse(sut.isLatestSuccessfulDeploymentNewerThan(new OctopusDate(2016, 2, 27)));
    }

    public void is_latest_successful_deployment_newer_than_returns_false_if_passed_date_is_same() {
        Environment sut = new Environment("environment-1", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 26));
        Assert.assertFalse(sut.isLatestSuccessfulDeploymentNewerThan(new OctopusDate(2016, 2, 26)));
    }

    public void is_latest_successful_deployment_newer_than_returns_true_if_passed_date_is_older() {
        Environment sut = new Environment("environment-1", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 26));
        Assert.assertTrue(sut.isLatestSuccessfulDeploymentNewerThan(new OctopusDate(2016, 2, 25)));
    }

    public void two_param_ctor_sets_latest_successful_date_to_null() {
        Environment sut = new Environment("environment-1", new OctopusDate(2016, 2, 26));
        Assert.assertEquals(sut.latestSuccessfulDeployment.getClass(), NullOctopusDate.class);
    }

    public void is_successful_returns_true_if_dates_are_the_same() {
        Environment sut = new Environment("environment-1", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 26));
        Assert.assertTrue(sut.wasLatestDeploymentSuccessful());
    }

    public void is_successful_returns_false_if_dates_are_different() {
        Environment sut = new Environment("environment-1", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 25));
        Assert.assertFalse(sut.wasLatestDeploymentSuccessful());
    }

    public void has_had_at_least_one_successful_deployment_returns_true_if_date_of_last_successful_deployment_is_newer_than_1970() {
        Environment sut = new Environment("environment-1", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 25));
        Assert.assertTrue(sut.hasHadAtLeastOneSuccessfulDeployment());
    }

    public void has_had_at_least_one_successful_deployment_returns_false_if_date_of_last_successful_deployment_is_1970() {
        Environment sut = new Environment("environment-1", new OctopusDate(2016, 2, 26), new NullOctopusDate());
        Assert.assertFalse(sut.hasHadAtLeastOneSuccessfulDeployment());
    }

    public void equals_returns_false_when_other_object_is_not_a_deployment() {
        Environment sut = new Environment("environment-1", new OctopusDate(2016, 2, 26), new NullOctopusDate());
        Assert.assertFalse(sut.equals(new Environments()));
    }

    public void equals_returns_false_when_other_object_is_null() {
        Environment sut = new Environment("environment-1", new OctopusDate(2016, 2, 26), new NullOctopusDate());
        Assert.assertFalse(sut.equals(null));
    }

    public void equals_returns_true_when_both_objects_are_same() {
        Environment sut = new Environment("environment-1", new OctopusDate(2016, 2, 26), new NullOctopusDate());
        Environment other = new Environment("environment-1", new OctopusDate(2016, 2, 26), new NullOctopusDate());
        Assert.assertTrue(sut.equals(other));
    }
}
