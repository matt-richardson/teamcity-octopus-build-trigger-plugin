package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;


import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class ReleasesProviderFactoryTest {
    public void get_provider_returns_releases_provider() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        ReleasesProviderFactory sut = new ReleasesProviderFactory();
        String url = "the-url";
        String apiKey = "the-api-key";
        Integer connectionTimeout = 100;
        ReleasesProvider result = sut.getProvider(url, apiKey, connectionTimeout);

        Assert.assertEquals(result.getClass(), ReleasesProviderImpl.class);
    }
}
