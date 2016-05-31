package com.mjrichardson.teamCity.buildTriggers.Model;

import com.mjrichardson.teamCity.buildTriggers.Model.Deployment;
import com.mjrichardson.teamCity.buildTriggers.Model.Deployments;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

@Test
public class DeploymentsTest {
    private Deployment getOctopusDeployment(String id) {
        HashMap<String, Object> linksMap = new HashMap<>();
        linksMap.put("Task", "/api/tasks/ServerTasks-770");
        linksMap.put("Release", "/api/releases/Release-18");

        HashMap<String, Object> map = new HashMap<>();
        map.put("Id", id);
        map.put("EnvironmentId", "Environments-1");
        map.put("ProjectId", "Project-12");
        map.put("ReleaseId", "Release-12");
        map.put("EnvironmentId", "Environments-1");
        map.put("Created", "2016-03-09T05:29:31.768+00:00");
        map.put("Links", linksMap);

        return Deployment.Parse(map);
    }

    public void contains_returns_true_when_octopus_deployment_with_specified_id_exists() throws Exception {
        Deployments deployments = new Deployments();
        deployments.add(getOctopusDeployment("Deployments-1"));
        deployments.add(getOctopusDeployment("Deployments-2"));
        Assert.assertTrue(deployments.contains("Deployments-2"));
        Assert.assertEquals(deployments.size(), 2);
    }

    public void contains_returns_false_when_octopus_deployment_with_specified_id_does_not_exist() throws Exception {
        Deployments deployments = new Deployments();
        deployments.add(getOctopusDeployment("Deployments-1"));
        deployments.add(getOctopusDeployment("Deployments-2"));
        Assert.assertFalse(deployments.contains("Deployments-3"));
    }

    public void can_iterate_through_results() {
        Deployments deployments = new Deployments();
        deployments.add(getOctopusDeployment("Deployments-1"));
        deployments.add(getOctopusDeployment("Deployments-2"));

        Assert.assertEquals(deployments.size(), 2);

        Integer counter = 1;
        for (Deployment octopusDeployment : deployments) {
            Assert.assertEquals(octopusDeployment.deploymentId, "Deployments-" + counter++);
        }
    }

    public void to_array_returns_all_items_as_array() {
        Deployments deployments = new Deployments();
        deployments.add(getOctopusDeployment("Deployments-1"));
        deployments.add(getOctopusDeployment("Deployments-2"));

        Deployment[] array = deployments.toArray();
        Assert.assertEquals(array.length, 2);
        Assert.assertEquals(array[0].deploymentId, "Deployments-1");
        Assert.assertEquals(array[1].deploymentId, "Deployments-2");
    }
}
