package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.ParseException;

@Test
public class EnvironmentsTest {
    public void can_convert_single_environment_from_string_and_back_again() throws Exception {
        final String expected = "Environments-1;2015-12-08T08:09:39.624+00:00;2015-11-12T09:22:00.865+00:00";
        Environments environments = Environments.Parse(expected);
        Assert.assertEquals(environments.toString(), expected);
    }

    public void can_convert_multiple_environments_from_string_and_back_again() throws Exception {
        final String expected = "Environments-1;2015-12-08T08:09:39.624+00:00;2015-11-12T09:22:00.865+00:00|Environments-2;2015-12-07T14:12:14.624+00:00;2015-12-07T14:12:14.624+00:00";
        Environments environments = Environments.Parse(expected);
        Assert.assertEquals(environments.toString(), expected);
    }

    public void can_convert_from_empty_string_and_back_again() throws Exception {
        Environments environments = Environments.Parse("");
        Assert.assertEquals(environments.toString(), "");
    }

    public void no_arg_ctor_returns_empty() throws Exception {
        Environments environments = new Environments();
        Assert.assertEquals(environments.toString(), "");
        Assert.assertTrue(environments.isEmpty());
    }

    public void is_empty_returns_true_when_no_deployments() throws Exception {
        Environments environments = Environments.Parse("");
        Assert.assertTrue(environments.isEmpty());
    }

    public void is_empty_returns_false_when_has_deployments() throws Exception {
        final String expected = "Environments-1;2015-12-08T08:09:39.624+00:00;2015-11-12T09:22:00.865+00:00|Environments-2;2015-12-07T14:12:14.624+00:00;2015-12-07T14:12:14.624+00:00";
        Environments environments = Environments.Parse(expected);
        Assert.assertFalse(environments.isEmpty());
    }

    public void passing_single_deployment_to_ctor_adds_to_collection() throws Exception {
        final Environment environment = new Environment("Environments-1", OctopusDate.Parse("2015-12-08T08:09:39.624+00:00"), OctopusDate.Parse("2015-11-12T09:22:00.865+00:00"));
        Environments environments = new Environments(environment);
        Assert.assertEquals(environments.size(), 1);
        Assert.assertEquals(environments.toString(), "Environments-1;2015-12-08T08:09:39.624+00:00;2015-11-12T09:22:00.865+00:00");
    }

    public void trim_multiple_deployments_to_return_only_one_changed_environment() throws Exception {
        final String oldData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00";
        Environments oldEnvironments = Environments.Parse(oldData);
        final String newData = "Environments-1;2016-01-21T14:26:14.747+00:00;2016-01-21T14:25:40.247+00:00|Environments-21;2016-01-21T14:25:53.700+00:00;2016-01-21T14:25:53.700+00:00";
        Environments newEnvironments = Environments.Parse(newData);

        final Environments trimmedEnvironments = newEnvironments.trimToOnlyHaveMaximumOneChangedEnvironment(oldEnvironments);
        Assert.assertEquals(trimmedEnvironments.size(), 2);
        Environment environment = trimmedEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2016, 1, 21, 14, 26, 14, 747), new OctopusDate(2016, 1, 21, 14, 25, 40, 247)));
        environment = trimmedEnvironments.getEnvironment("Environments-21");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-21", new OctopusDate(2016, 1, 20, 14, 0, 0, 0), new OctopusDate(2016, 1, 20, 14, 0, 0, 0)));
    }

    public void trim_multiple_deployments_to_return_only_one_changed_environment_can_prioritise_successful_deployments() throws Exception {
        final String oldData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00";
        Environments oldEnvironments = Environments.Parse(oldData);
        final String newData = "Environments-1;2016-01-21T14:26:14.747+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-21T14:25:53.700+00:00;2016-01-21T14:25:53.700+00:00";
        Environments newEnvironments = Environments.Parse(newData);

        final Boolean prioritiseSuccessfulDeployments = true;
        final Environments trimmedEnvironments = newEnvironments.trimToOnlyHaveMaximumOneChangedEnvironment(oldEnvironments, prioritiseSuccessfulDeployments);
        Assert.assertEquals(trimmedEnvironments.size(), 2);
        Environment environment = trimmedEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2016, 1, 19, 14, 0, 0, 0), new OctopusDate(2016, 1, 19, 0, 0, 0, 0)));
        environment = trimmedEnvironments.getEnvironment("Environments-21");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-21", new OctopusDate(2016, 1, 21, 14, 25, 53, 700), new OctopusDate(2016, 1, 21, 14, 25, 53, 700)));
    }

    public void trim_multiple_deployments_to_return_only_one_changed_environment_can_skip_successful_deployment_prioritisation() throws Exception {
        final String oldData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00";
        Environments oldEnvironments = Environments.Parse(oldData);
        final String newData = "Environments-1;2016-01-21T14:26:14.747+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-21T14:25:53.700+00:00;2016-01-21T14:25:53.700+00:00";
        Environments newEnvironments = Environments.Parse(newData);

        final Boolean prioritiseSuccessfulDeployments = false;
        final Environments trimmedEnvironments = newEnvironments.trimToOnlyHaveMaximumOneChangedEnvironment(oldEnvironments, prioritiseSuccessfulDeployments);
        Assert.assertEquals(trimmedEnvironments.size(), 2);
        Environment environment = trimmedEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2016, 1, 21, 14, 26, 14, 747), new OctopusDate(2016, 1, 19, 0, 0, 0, 0)));
        environment = trimmedEnvironments.getEnvironment("Environments-21");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-21", new OctopusDate(2016, 1, 20, 14, 0, 0, 0), new OctopusDate(2016, 1, 20, 14, 0, 0, 0)));
    }

    public void trim_multiple_deployments_to_return_only_one_changed_environment_returns_input_when_none_changed() throws Exception {
        final String oldData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00";
        Environments oldEnvironments = Environments.Parse(oldData);
        final String newData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00";
        Environments newEnvironments = Environments.Parse(newData);

        final Boolean prioritiseSuccessfulDeployments = true;
        final Environments trimmedEnvironments = newEnvironments.trimToOnlyHaveMaximumOneChangedEnvironment(oldEnvironments, prioritiseSuccessfulDeployments);
        Assert.assertEquals(trimmedEnvironments.size(), 2);
        Environment environment = trimmedEnvironments.getEnvironment("Environments-1");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-1", new OctopusDate(2016, 1, 19, 14, 0, 0, 0), new OctopusDate(2016, 1, 19, 0, 0, 0, 0)));
        environment = trimmedEnvironments.getEnvironment("Environments-21");
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-21", new OctopusDate(2016, 1, 20, 14, 0, 0, 0), new OctopusDate(2016, 1, 20, 14, 0, 0, 0)));
    }

    public void get_changed_deployment_returns_first_environment_thats_changed() throws Exception {
        final String oldData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00";
        Environments oldEnvironments = Environments.Parse(oldData);
        final String newData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-21T14:25:53.700+00:00;2016-01-21T14:25:53.700+00:00";
        Environments newEnvironments = Environments.Parse(newData);

        Environment environment = newEnvironments.getChangedDeployment(oldEnvironments);
        Assert.assertNotNull(environment);
        Assert.assertEquals(environment, new Environment("Environments-21", new OctopusDate(2016, 1, 21, 14, 25, 53, 700), new OctopusDate(2016, 1, 21, 14, 25, 53, 700)));
    }

    @Test(expectedExceptions = NoChangedEnvironmentsException.class)
    public void get_changed_deployment_throws_exception_when_none_changed() throws Exception {
        final String oldData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00";
        Environments oldEnvironments = Environments.Parse(oldData);
        final String newData = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00";
        Environments newEnvironments = Environments.Parse(newData);

        newEnvironments.getChangedDeployment(oldEnvironments);
    }

    public void to_array_converts_deployments_to_array_successfully() throws ParseException {
        final String data = "Environments-1;2016-01-19T14:00:00.000+00:00;2016-01-19T00:00:00.000+00:00|Environments-21;2016-01-20T14:00:00.000+00:00;2016-01-20T14:00:00.000+00:00";
        Environments environments = Environments.Parse(data);
        Environment[] array = environments.toArray();
        Assert.assertEquals(array.length, 2);
        Assert.assertEquals(array[0], new Environment("Environments-1", new OctopusDate(2016, 1, 19, 14, 0, 0), new OctopusDate(2016, 1, 19)));
        Assert.assertEquals(array[1], new Environment("Environments-21", new OctopusDate(2016, 1, 20, 14, 0, 0), new OctopusDate(2016, 1, 20, 14, 0, 0)));
    }

    public void add_environment_adds_with_null_dates() {
        Environments sut = new Environments();
        sut.addEnvironment("env-id");
        Environment[] result = sut.toArray();
        Assert.assertEquals(result[0].environmentId, "env-id");
        Assert.assertEquals(result[0].latestSuccessfulDeployment.getClass(), NullOctopusDate.class);
        Assert.assertEquals(result[0].latestDeployment.getClass(), NullOctopusDate.class);
    }

    public void add_environment_does_not_add_again_if_already_exists() {
        Environments sut = new Environments();
        sut.addEnvironment("env-id");
        sut.addEnvironment("env-id");
        Assert.assertEquals(sut.toArray().length, 1);
    }

    public void have_all_environments_had_at_least_one_successful_deployment_returns_true_if_all_match() {
        Environments sut = new Environments();
        sut.addOrUpdate(new Environment("Environment-1", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 29)));
        sut.addOrUpdate(new Environment("Environment-2", new OctopusDate(2016, 2, 28), new OctopusDate(2016, 1, 16)));
        Assert.assertTrue(sut.haveAllEnvironmentsHadAtLeastOneSuccessfulDeployment());
    }

    public void have_all_environments_had_at_least_one_successful_deployment_returns_false_if_not_all_match() {
        Environments sut = new Environments();
        sut.addOrUpdate(new Environment("Environment-1", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 29)));
        sut.addOrUpdate(new Environment("Environment-2", new OctopusDate(2016, 2, 28), new NullOctopusDate()));
        Assert.assertFalse(sut.haveAllEnvironmentsHadAtLeastOneSuccessfulDeployment());
    }

    public void add_or_update_with_multiple_deployments_adds_all() {
        Environments newEnvironments = new Environments();
        newEnvironments.addOrUpdate(new Environment("Environment-1", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 29)));
        newEnvironments.addOrUpdate(new Environment("Environment-2", new OctopusDate(2016, 2, 28), new NullOctopusDate()));
        Environments sut = new Environments();
        sut.addOrUpdate(newEnvironments);

        Assert.assertEquals(sut.size(), 2);
        Assert.assertEquals(sut.toString(), newEnvironments.toString());
    }

    public void add_or_update_with_multiple_deployments_overwrites_if_latest_deployment_date_is_newer() {
        Environments newEnvironments = new Environments();
        newEnvironments.addOrUpdate(new Environment("Environment-1", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28)));
        newEnvironments.addOrUpdate(new Environment("Environment-2", new OctopusDate(2016, 2, 28), new NullOctopusDate()));

        Environments sut = new Environments(new Environment("Environment-1", new OctopusDate(2016, 2, 28), new OctopusDate(2016, 2, 28)));
        sut.addOrUpdate(newEnvironments);

        Assert.assertEquals(sut.getEnvironment("Environment-1").latestDeployment, new OctopusDate(2016, 2, 29));
        Assert.assertEquals(sut.getEnvironment("Environment-1").latestSuccessfulDeployment, new OctopusDate(2016, 2, 28));
    }

    public void add_or_update_with_multiple_deployments_overwrites_if_latest_successful_deployment_date_is_newer() {
        Environments newEnvironments = new Environments();
        newEnvironments.addOrUpdate(new Environment("Environment-1", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 29)));
        newEnvironments.addOrUpdate(new Environment("Environment-2", new OctopusDate(2016, 2, 28), new NullOctopusDate()));

        Environments sut = new Environments(new Environment("Environment-1", new OctopusDate(2016, 2, 28), new OctopusDate(2016, 2, 28)));
        sut.addOrUpdate(newEnvironments);

        Assert.assertEquals(sut.getEnvironment("Environment-1").latestDeployment, new OctopusDate(2016, 2, 29));
        Assert.assertEquals(sut.getEnvironment("Environment-1").latestSuccessfulDeployment, new OctopusDate(2016, 2, 29));
    }

    public void add_or_update_with_multiple_deployments_doesnt_overwrite_if_dates_are_older() {
        Environments newEnvironments = new Environments();
        newEnvironments.addOrUpdate(new Environment("Environment-1", new OctopusDate(2016, 2, 27), new OctopusDate(2016, 2, 27)));
        newEnvironments.addOrUpdate(new Environment("Environment-2", new OctopusDate(2016, 2, 28), new NullOctopusDate()));

        Environments sut = new Environments(new Environment("Environment-1", new OctopusDate(2016, 2, 28), new OctopusDate(2016, 2, 28)));
        sut.addOrUpdate(newEnvironments);

        Assert.assertEquals(sut.getEnvironment("Environment-1").latestDeployment, new OctopusDate(2016, 2, 28));
        Assert.assertEquals(sut.getEnvironment("Environment-1").latestDeployment, new OctopusDate(2016, 2, 28));
    }

    public void add_or_update_with_single_deployment_that_is_null_does_not_add() {
        Environments sut = new Environments();
        sut.addOrUpdate(new NullEnvironment());
        Assert.assertTrue(sut.isEmpty());
    }

    public void add_or_update_adds_if_not_exists() {
        Environments sut = new Environments();
        sut.addOrUpdate(new Environment("env-id", new OctopusDate(2016, 2, 29), new NullOctopusDate()));
        Assert.assertEquals(sut.size(), 1);

        Environment[] environments = sut.toArray();
        Assert.assertEquals(environments[0].environmentId, "env-id");
        Assert.assertEquals(environments[0].latestDeployment, new OctopusDate(2016, 2, 29));
        Assert.assertEquals(environments[0].latestSuccessfulDeployment.getClass(), NullOctopusDate.class);
    }

    public void add_or_update_updates_if_passed_latest_deployment_date_is_newer() {
        Environments sut = new Environments(new Environment("env-id", new OctopusDate(2016, 2, 28)));
        sut.addOrUpdate(new Environment("env-id", new OctopusDate(2016, 2, 29), new NullOctopusDate()));
        Assert.assertEquals(sut.size(), 1);

        Environment[] environments = sut.toArray();
        Assert.assertEquals(environments[0].environmentId, "env-id");
        Assert.assertEquals(environments[0].latestDeployment, new OctopusDate(2016, 2, 29));
        Assert.assertEquals(environments[0].latestSuccessfulDeployment.getClass(), NullOctopusDate.class);
    }

    public void add_or_update_updates_if_passed_latest_deployment_date_is_newer_and_deployment_successful() {
        Environments sut = new Environments(new Environment("env-id", new OctopusDate(2016, 2, 28), new OctopusDate(2016, 2, 28)));
        sut.addOrUpdate(new Environment("env-id", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 29)));
        Assert.assertEquals(sut.size(), 1);

        Environment[] environments = sut.toArray();
        Assert.assertEquals(environments[0].environmentId, "env-id");
        Assert.assertEquals(environments[0].latestDeployment, new OctopusDate(2016, 2, 29));
        Assert.assertEquals(environments[0].latestSuccessfulDeployment, new OctopusDate(2016, 2, 29));
    }

    public void add_or_update_does_not_overwrite_if_passed_date_is_older() {
        Environments sut = new Environments(new Environment("env-id", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 29)));
        sut.addOrUpdate(new Environment("env-id", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 26)));
        Assert.assertEquals(sut.size(), 1);

        Environment[] environments = sut.toArray();
        Assert.assertEquals(environments[0].environmentId, "env-id");
        Assert.assertEquals(environments[0].latestDeployment, new OctopusDate(2016, 2, 29));
        Assert.assertEquals(environments[0].latestSuccessfulDeployment, new OctopusDate(2016, 2, 29));
    }

    public void equals_returns_false_if_passed_object_is_null() {
        Environments sut = new Environments(new Environment("env-id", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 29)));
        Assert.assertFalse(sut.equals(null));
    }

    public void equals_returns_false_if_passed_object_is_different_class() {
        Environments sut = new Environments(new Environment("env-id", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 29)));
        Assert.assertFalse(sut.equals("a random string"));
    }

    public void equals_returns_false_if_passed_deployments_has_different_size() {
        Environments sut = new Environments();
        sut.addOrUpdate(new Environment("env-1", new OctopusDate(2016, 2, 29), new NullOctopusDate()));
        sut.addOrUpdate(new Environment("env-2", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 26)));
        Environments other = new Environments();
        other.addOrUpdate(new Environment("env-1", new OctopusDate(2016, 2, 29), new NullOctopusDate()));
        Assert.assertFalse(sut.equals(other));
    }

    public void equals_returns_false_if_different_environments() {
        Environments sut = new Environments();
        sut.addOrUpdate(new Environment("env-1", new OctopusDate(2016, 2, 29), new NullOctopusDate()));
        sut.addOrUpdate(new Environment("env-2", new OctopusDate(2016, 2, 26), new OctopusDate(2016, 2, 26)));
        Environments other = new Environments();
        other.addOrUpdate(new Environment("env-1", new OctopusDate(2016, 2, 29), new NullOctopusDate()));
        other.addOrUpdate(new Environment("env-3", new OctopusDate(2016, 2, 25), new NullOctopusDate()));
        Assert.assertFalse(sut.equals(other));
    }

    public void equals_returns_false_if_latest_deployment_date_different() {
        Environments sut = new Environments();
        sut.addOrUpdate(new Environment("env-1", new OctopusDate(2016, 2, 29), new NullOctopusDate()));
        sut.addOrUpdate(new Environment("env-2", new OctopusDate(2016, 2, 26), new NullOctopusDate()));
        Environments other = new Environments();
        other.addOrUpdate(new Environment("env-1", new OctopusDate(2016, 2, 29), new NullOctopusDate()));
        other.addOrUpdate(new Environment("env-2", new OctopusDate(2016, 2, 25), new NullOctopusDate()));
        Assert.assertFalse(sut.equals(other));
    }

    public void equals_returns_false_if_latest_successful_deployment_different() {
        Environments sut = new Environments();
        sut.addOrUpdate(new Environment("env-1", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28)));
        sut.addOrUpdate(new Environment("env-2", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28)));
        Environments other = new Environments();
        other.addOrUpdate(new Environment("env-1", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 27)));
        other.addOrUpdate(new Environment("env-2", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28)));
        Assert.assertFalse(sut.equals(other));
    }

    public void equals_returns_true_if_contents_are_identical() {
        Environments sut = new Environments();
        sut.addOrUpdate(new Environment("env-1", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28)));
        sut.addOrUpdate(new Environment("env-2", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28)));
        Environments other = new Environments();
        other.addOrUpdate(new Environment("env-1", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28)));
        other.addOrUpdate(new Environment("env-2", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28)));
        Assert.assertTrue(sut.equals(other));
    }

    public void contains_returns_false_if_no_match() {
        final Environment oldEnvironment = new Environment("env-1", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28));
        final Environment newEnvironment = new Environment("env-3", new OctopusDate(2016, 2, 25), new OctopusDate(2016, 2, 25));
        Environments environments = new Environments();
        environments.addOrUpdate(oldEnvironment);
        environments.addOrUpdate(newEnvironment);
        Assert.assertFalse(environments.contains(new Environment("env-2", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28))));
    }

    public void contains_returns_true_if_match() {
        final Environment oldEnvironment = new Environment("env-1", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28));
        final Environment newEnvironment = new Environment("env-3", new OctopusDate(2016, 2, 25), new OctopusDate(2016, 2, 25));
        Environments environments = new Environments();
        environments.addOrUpdate(oldEnvironment);
        environments.addOrUpdate(newEnvironment);
        Assert.assertTrue(environments.contains(new Environment("env-3", new OctopusDate(2016, 2, 25), new OctopusDate(2016, 2, 25))));
    }

    public void remove_removes_specified_environments() {
        final Environment environmentOne = new Environment("env-1", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28));
        final Environment environmentTwo = new Environment("env-2", new OctopusDate(2016, 2, 29), new OctopusDate(2016, 2, 28));
        final Environment environmentThree = new Environment("env-3", new OctopusDate(2016, 2, 25), new OctopusDate(2016, 2, 25));
        Environments environments = new Environments();
        environments.addOrUpdate(environmentOne);
        environments.addOrUpdate(environmentTwo);
        environments.addOrUpdate(environmentThree);

        Environments newEnvironments = new Environments();
        newEnvironments.addOrUpdate(environmentOne);
        newEnvironments.addOrUpdate(environmentTwo);

        Environments deleted = environments.removeEnvironmentsNotIn(newEnvironments);

        Assert.assertEquals(environments.size(), 2);
        Assert.assertTrue(environments.contains(environmentOne));
        Assert.assertTrue(environments.contains(environmentTwo));

        Assert.assertEquals(deleted.size(), 1);
        Assert.assertTrue(deleted.contains(environmentThree));
    }

    public void can_create_from_deployment_task_and_release() throws org.json.simple.parser.ParseException, IOException {
        Deployment deployment = new Deployment("the-deployment-id", "the-environment-id", new OctopusDate(2016, 8, 4), "the-task-link", "the-release-id", "the-project-id", "the-release-link");
        String json = ResourceHandler.getResource("api/tasks/ServerTasks-272");
        ApiTaskResponse task = new ApiTaskResponse(json);
        json = ResourceHandler.getResource("api/releases/Releases-222");
        ApiReleaseResponse release = new ApiReleaseResponse(json);
        Environment result = Environment.CreateFrom(deployment, task, release);

        Assert.assertEquals(result.projectId, "the-project-id");
        Assert.assertEquals(result.environmentId, "the-environment-id");
        Assert.assertEquals(result.latestDeployment, new OctopusDate(2016, 8, 4));
        Assert.assertEquals(result.latestSuccessfulDeployment, new NullOctopusDate());
        Assert.assertEquals(result.releaseId, "the-release-id");
        Assert.assertEquals(result.deploymentId, "the-deployment-id");
        Assert.assertEquals(result.version, "0.0.9");
    }
}
