package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class HttpContentProviderFactoryTest {
    public void get_content_provider_returns_valid_object() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Integer connectionTimeoutInMilliseconds = 100;
        HttpContentProviderFactory sut = new HttpContentProviderFactory("url", "apikey", connectionTimeoutInMilliseconds, new FakeCacheManager());
        HttpContentProvider result = sut.getContentProvider();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getUrl(), "url");
    }
}
