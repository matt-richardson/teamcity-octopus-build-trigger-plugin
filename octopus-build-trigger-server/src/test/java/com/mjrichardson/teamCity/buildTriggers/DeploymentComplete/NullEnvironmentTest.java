package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.NullOctopusDate;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class NullEnvironmentTest {
    public void null_release_sets_fields_to_empty() {
        Environment sut = new NullEnvironment();
        Assert.assertEquals(sut.environmentId, "");
        Assert.assertEquals(sut.latestDeployment.getClass(), NullOctopusDate.class);
        Assert.assertEquals(sut.latestSuccessfulDeployment.getClass(), NullOctopusDate.class);
    }

    public void equals_returns_true_for_equal_null_deployment() {
        Environment sut = new NullEnvironment();
        Environment other = new NullEnvironment();
        Assert.assertTrue(sut.equals(other));
    }

    public void equals_returns_true_for_when_other_has_null_dates() {
        Environment sut = new NullEnvironment();
        Environment other = new Environment("", new NullOctopusDate(), new NullOctopusDate());
        Assert.assertTrue(sut.equals(other));
    }
}
