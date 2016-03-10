package com.mjrichardson.teamCity.buildTriggers.MachineAdded;


import com.mjrichardson.teamCity.buildTriggers.MachineAdded.MachinesProvider;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.MachinesProviderFactory;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.MachinesProviderImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class MachinesProviderFactoryTest {
    public void get_provider_returns_machines_provider() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        MachinesProviderFactory sut = new MachinesProviderFactory();
        String url = "the-url";
        String apiKey = "the-api-key";
        Integer connectionTimeoutInMilliseconds = 100;
        MachinesProvider result = sut.getProvider(url, apiKey, connectionTimeoutInMilliseconds);

        Assert.assertEquals(result.getClass(), MachinesProviderImpl.class);
    }
}
