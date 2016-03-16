package com.mjrichardson.teamCity.buildTriggers;


import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class AnalyticsTrackerImplTest {
    public void masks_private_detail_in_exception_messages() {
        Exception e = new Exception("com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentsProviderException: Unexpected exception in DeploymentsProviderImpl, while attempting to get deployments from http://windows10vm.local/: org.apache.http.conn.ConnectTimeoutException: Connect to windows10vm.local:80 [windows10vm.local/192.168.213.80] failed: connect timed out");
        String result = AnalyticsTrackerImpl.maskException(e);
        Assert.assertEquals(result, "java.lang.Exception: com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentsProviderException: Unexpected exception in DeploymentsProviderImpl, while attempting to get deployments from http://*****/: org.apache.http.conn.ConnectTimeoutException: Connect to *****:***** [*****/*****] failed: connect timed out");
    }
}
