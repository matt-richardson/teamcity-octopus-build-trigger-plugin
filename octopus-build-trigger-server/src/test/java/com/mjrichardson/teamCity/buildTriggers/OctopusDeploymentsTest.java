package com.mjrichardson.teamCity.buildTriggers;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

@Test
public class OctopusDeploymentsTest {
    private OctopusDeployment getOctopusDeployment(String id, String environmentId, OctopusDate created, String serverTaskLink) {
        HashMap<String, Object> linksMap = new HashMap<>();
        linksMap.put("Task", "/api/tasks/ServerTasks-770");

        HashMap<String, Object> map = new HashMap<>();
        map.put("Id", id);
        map.put("EnvironmentId", "Environments-1");
        map.put("Created", "2016-03-09T05:29:31.768+00:00");
        map.put("Links", linksMap);

        return OctopusDeployment.Parse(map);
    }

    public void contains_returns_true_when_octopus_deployment_with_specified_id_exists() throws Exception {
        OctopusDeployments octopusDeployments = new OctopusDeployments();
        octopusDeployments.add(getOctopusDeployment("Deployments-1", "Environments-1", new OctopusDate(2016, 3, 8), "/api/ServerTasks/Task-771"));
        octopusDeployments.add(getOctopusDeployment("Deployments-2", "Environments-1", new OctopusDate(2016, 3, 9), "/api/ServerTasks/Task-772"));
        Assert.assertTrue(octopusDeployments.contains("Deployments-2"));
        Assert.assertEquals(octopusDeployments.size(), 2);
    }

    public void contains_returns_false_when_octopus_deployment_with_specified_id_does_not_exist() throws Exception {
        OctopusDeployments octopusDeployments = new OctopusDeployments();
        octopusDeployments.add(getOctopusDeployment("Deployments-1", "Environments-1", new OctopusDate(2016, 3, 8), "/api/ServerTasks/Task-771"));
        octopusDeployments.add(getOctopusDeployment("Deployments-2", "Environments-1", new OctopusDate(2016, 3, 9), "/api/ServerTasks/Task-772"));
        Assert.assertFalse(octopusDeployments.contains("Deployments-3"));
    }

    public void can_iterate_through_results() {
        OctopusDeployments octopusDeployments = new OctopusDeployments();
        octopusDeployments.add(getOctopusDeployment("Deployments-1", "Environments-1", new OctopusDate(2016, 3, 8), "/api/ServerTasks/Task-771"));
        octopusDeployments.add(getOctopusDeployment("Deployments-2", "Environments-1", new OctopusDate(2016, 3, 9), "/api/ServerTasks/Task-772"));

        Assert.assertEquals(octopusDeployments.size(), 2);

        Integer counter = 1;
        for (OctopusDeployment octopusdeployment : octopusDeployments) {
            Assert.assertEquals(octopusdeployment.id, "Deployments-" + counter++);
        }
    }

    public void to_array_returns_all_items_as_array() {
        OctopusDeployments octopusDeployments = new OctopusDeployments();
        octopusDeployments.add(getOctopusDeployment("Deployments-1", "Environments-1", new OctopusDate(2016, 3, 8), "/api/ServerTasks/Task-771"));
        octopusDeployments.add(getOctopusDeployment("Deployments-2", "Environments-1", new OctopusDate(2016, 3, 9), "/api/ServerTasks/Task-772"));

        OctopusDeployment[] array = octopusDeployments.toArray();
        Assert.assertEquals(array.length, 2);
        Assert.assertEquals(array[0].id, "Deployments-1");
        Assert.assertEquals(array[1].id, "Deployments-2");
    }
}
