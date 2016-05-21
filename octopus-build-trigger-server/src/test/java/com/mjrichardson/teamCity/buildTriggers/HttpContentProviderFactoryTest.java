package com.mjrichardson.teamCity.buildTriggers;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeBuildTriggerProperties;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeMetricRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class HttpContentProviderFactoryTest {
    public void get_content_provider_returns_valid_object() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpContentProviderFactory sut = new HttpContentProviderFactory("url", "apikey", new FakeBuildTriggerProperties(), new FakeCacheManager(), new FakeMetricRegistry());
        HttpContentProvider result = sut.getContentProvider();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getUrl(), "url");
    }
}
