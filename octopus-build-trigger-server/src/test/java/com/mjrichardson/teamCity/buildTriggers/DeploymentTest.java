package com.mjrichardson.teamCity.buildTriggers;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

@Test
public class DeploymentTest {
    public void can_parse_valid_map() {
        HashMap<String, Object> linksMap = new HashMap<>();
        linksMap.put("Task", "/api/tasks/ServerTasks-770");

        HashMap<String, Object> map = new HashMap<>();
        map.put("Id", "Deployments-211");
        map.put("EnvironmentId", "Environments-1");
        map.put("Created", "2016-03-09T05:29:31.768+00:00");
        map.put("Links", linksMap);

        Deployment result = Deployment.Parse(map);
        Assert.assertEquals(result.deploymentId, "Deployments-211");
        Assert.assertEquals(result.environmentId, "Environments-1");
        Assert.assertEquals(result.createdDate, new OctopusDate(2016, 3, 9, 5, 29, 31, 768));
        Assert.assertEquals(result.taskLink, "/api/tasks/ServerTasks-770");
    }
}
