package com.mjrichardson.teamCity.buildTriggers;


import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class AnalyticsTrackerImplTest {
    public void masks_private_detail_in_exception_messages_for_http() {
        Exception e = new Exception("com.mjrichardson.teamCity.buildTriggers.Exceptions.DeploymentsProviderException: Unexpected exception in DeploymentsProviderImpl, while attempting to get deployments from http://windows10vm.local/: org.apache.http.conn.ConnectTimeoutException: Connect to windows10vm.local:80 [windows10vm.local/192.168.213.80] failed: connect timed out");
        String result = AnalyticsTrackerImpl.maskException(e);
        Assert.assertEquals(result, "java.lang.Exception: com.mjrichardson.teamCity.buildTriggers.Exceptions.DeploymentsProviderException: Unexpected exception in DeploymentsProviderImpl, while attempting to get deployments from http://*****/: org.apache.http.conn.ConnectTimeoutException: Connect to *****:***** [*****/*****] failed: connect timed out");
    }

    public void masks_private_detail_in_exception_messages_for_connect_timeout_exception_for_https() {
        Exception e = new Exception("com.mjrichardson.teamCity.buildTriggers.Exceptions.DeploymentsProviderException: Unexpected exception in DeploymentsProviderImpl, while attempting to get deployments from https://windows10vm.local/: org.apache.http.conn.ConnectTimeoutException: Connect to windows10vm.local:80 [windows10vm.local/192.168.213.80] failed: connect timed out");
        String result = AnalyticsTrackerImpl.maskException(e);
        Assert.assertEquals(result, "java.lang.Exception: com.mjrichardson.teamCity.buildTriggers.Exceptions.DeploymentsProviderException: Unexpected exception in DeploymentsProviderImpl, while attempting to get deployments from https://*****/: org.apache.http.conn.ConnectTimeoutException: Connect to *****:***** [*****/*****] failed: connect timed out");
    }

    public void masks_private_detail_in_exception_messages_for_host_connect_exception_for_http() {
        Exception e = new Exception("Unexpected exception in MachinesProviderImpl, while attempting to get Machines from http://windows10vm.local: org.apache.http.conn.HttpHostConnectException: Connect to windows10vm.local:443 [windows10vm.local/192.168.213.80] failed: Connection timed out: connect ");
        String result = AnalyticsTrackerImpl.maskException(e);
        Assert.assertEquals(result, "java.lang.Exception: Unexpected exception in MachinesProviderImpl, while attempting to get Machines from http://*****: org.apache.http.conn.HttpHostConnectException: Connect to *****:***** [*****/*****] failed: Connection timed out: connect ");
    }

    public void masks_private_detail_in_exception_messages_for_host_connect_exception_for_https() {
        Exception e = new Exception("Unexpected exception in MachinesProviderImpl, while attempting to get Machines from https://windows10vm.local: org.apache.http.conn.HttpHostConnectException: Connect to windows10vm.local:443 [windows10vm.local/192.168.213.80] failed: Connection timed out: connect ");
        String result = AnalyticsTrackerImpl.maskException(e);
        Assert.assertEquals(result, "java.lang.Exception: Unexpected exception in MachinesProviderImpl, while attempting to get Machines from https://*****: org.apache.http.conn.HttpHostConnectException: Connect to *****:***** [*****/*****] failed: Connection timed out: connect ");
    }
}
