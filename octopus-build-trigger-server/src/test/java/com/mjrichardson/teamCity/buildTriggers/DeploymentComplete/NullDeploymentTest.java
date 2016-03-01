package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.NullOctopusDate;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class NullDeploymentTest {
    public void null_release_sets_fields_to_empty() {
        Deployment sut = new NullDeployment();
        Assert.assertEquals(sut.environmentId, "");
        Assert.assertEquals(sut.latestDeployment.getClass(), NullOctopusDate.class);
        Assert.assertEquals(sut.latestSuccessfulDeployment.getClass(), NullOctopusDate.class);
    }

    public void equals_returns_true_for_equal_null_deployment() {
        Deployment sut = new NullDeployment();
        Deployment other = new NullDeployment();
        Assert.assertTrue(sut.equals(other));
    }
}
