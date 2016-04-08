package com.mjrichardson.teamCity.buildTriggers;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

@Test
public class DeploymentsTest {
    private Deployment getOctopusDeployment(String id, String environmentId, OctopusDate created, String serverTaskLink) {
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
        deployments.add(getOctopusDeployment("Deployments-1", "Environments-1", new OctopusDate(2016, 3, 8), "/api/ServerTasks/Task-771"));
        deployments.add(getOctopusDeployment("Deployments-2", "Environments-1", new OctopusDate(2016, 3, 9), "/api/ServerTasks/Task-772"));
        Assert.assertTrue(deployments.contains("Deployments-2"));
        Assert.assertEquals(deployments.size(), 2);
    }

    public void contains_returns_false_when_octopus_deployment_with_specified_id_does_not_exist() throws Exception {
        Deployments deployments = new Deployments();
        deployments.add(getOctopusDeployment("Deployments-1", "Environments-1", new OctopusDate(2016, 3, 8), "/api/ServerTasks/Task-771"));
        deployments.add(getOctopusDeployment("Deployments-2", "Environments-1", new OctopusDate(2016, 3, 9), "/api/ServerTasks/Task-772"));
        Assert.assertFalse(deployments.contains("Deployments-3"));
    }

    public void can_iterate_through_results() {
        Deployments deployments = new Deployments();
        deployments.add(getOctopusDeployment("Deployments-1", "Environments-1", new OctopusDate(2016, 3, 8), "/api/ServerTasks/Task-771"));
        deployments.add(getOctopusDeployment("Deployments-2", "Environments-1", new OctopusDate(2016, 3, 9), "/api/ServerTasks/Task-772"));

        Assert.assertEquals(deployments.size(), 2);

        Integer counter = 1;
        for (Deployment octopusdeployment : deployments) {
            Assert.assertEquals(octopusdeployment.deploymentId, "Deployments-" + counter++);
        }
    }

    public void to_array_returns_all_items_as_array() {
        Deployments deployments = new Deployments();
        deployments.add(getOctopusDeployment("Deployments-1", "Environments-1", new OctopusDate(2016, 3, 8), "/api/ServerTasks/Task-771"));
        deployments.add(getOctopusDeployment("Deployments-2", "Environments-1", new OctopusDate(2016, 3, 9), "/api/ServerTasks/Task-772"));

        Deployment[] array = deployments.toArray();
        Assert.assertEquals(array.length, 2);
        Assert.assertEquals(array[0].deploymentId, "Deployments-1");
        Assert.assertEquals(array[1].deploymentId, "Deployments-2");
    }
}
