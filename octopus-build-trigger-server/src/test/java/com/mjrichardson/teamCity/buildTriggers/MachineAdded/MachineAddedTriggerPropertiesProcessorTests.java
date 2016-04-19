package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeMetricRegistry;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeOctopusConnectivityCheckerFactory;
import jetbrains.buildServer.serverSide.InvalidProperty;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

@Test
public class MachineAddedTriggerPropertiesProcessorTests {
    public void returns_error_when_url_is_null() {
        MachineAddedTriggerPropertiesProcessor processor = new MachineAddedTriggerPropertiesProcessor(new FakeCacheManager(), new FakeMetricRegistry());
        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_APIKEY, "api key");
        properties.put(OCTOPUS_URL, null);
        Collection<InvalidProperty> result = processor.process(properties);

        Assert.assertEquals(result.size(), 1);
        InvalidProperty invalidProperty = (InvalidProperty) result.toArray()[0];
        Assert.assertEquals(invalidProperty.getPropertyName(), OCTOPUS_URL);
        Assert.assertEquals(invalidProperty.getInvalidReason(), "URL must be specified");
    }

    public void returns_error_when_api_key_is_null() {
        MachineAddedTriggerPropertiesProcessor processor = new MachineAddedTriggerPropertiesProcessor(new FakeCacheManager(), new FakeMetricRegistry());
        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_APIKEY, null);
        properties.put(OCTOPUS_URL, "http://example.com");
        Collection<InvalidProperty> result = processor.process(properties);

        Assert.assertEquals(result.size(), 1);
        InvalidProperty invalidProperty = (InvalidProperty) result.toArray()[0];
        Assert.assertEquals(invalidProperty.getPropertyName(), OCTOPUS_APIKEY);
        Assert.assertEquals(invalidProperty.getInvalidReason(), "API Key must be specified");
    }

    public void returns_error_if_connectivity_checker_returns_error() {
        String connectivityCheckResult = "connectivity error";
        FakeOctopusConnectivityCheckerFactory octopusConnectivityCheckerFactory = new FakeOctopusConnectivityCheckerFactory(connectivityCheckResult);
        MachineAddedTriggerPropertiesProcessor processor = new MachineAddedTriggerPropertiesProcessor(octopusConnectivityCheckerFactory);
        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_APIKEY, "API-KEY");
        properties.put(OCTOPUS_URL, "api key");
        Collection<InvalidProperty> result = processor.process(properties);

        Assert.assertEquals(result.size(), 1);
        InvalidProperty invalidProperty = (InvalidProperty) result.toArray()[0];
        Assert.assertEquals(invalidProperty.getPropertyName(), OCTOPUS_URL);
        Assert.assertEquals(invalidProperty.getInvalidReason(), "connectivity error");
    }

    public void returns_error_if_connectivity_checker_throws_exception() {
        NoSuchAlgorithmException exception = new NoSuchAlgorithmException("the exception message");
        FakeOctopusConnectivityCheckerFactory octopusConnectivityCheckerFactory = new FakeOctopusConnectivityCheckerFactory(exception);
        MachineAddedTriggerPropertiesProcessor processor = new MachineAddedTriggerPropertiesProcessor(octopusConnectivityCheckerFactory);
        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_APIKEY, "API-KEY");
        properties.put(OCTOPUS_URL, "api key");
        Collection<InvalidProperty> result = processor.process(properties);

        Assert.assertEquals(result.size(), 1);
        InvalidProperty invalidProperty = (InvalidProperty) result.toArray()[0];
        Assert.assertEquals(invalidProperty.getPropertyName(), OCTOPUS_URL);
        Assert.assertEquals(invalidProperty.getInvalidReason(), "the exception message");
    }

    public void returns_empty_result_if_everything_is_valid() {
        String connectivityCheckResult = "";
        FakeOctopusConnectivityCheckerFactory octopusConnectivityCheckerFactory = new FakeOctopusConnectivityCheckerFactory(connectivityCheckResult);
        MachineAddedTriggerPropertiesProcessor processor = new MachineAddedTriggerPropertiesProcessor(octopusConnectivityCheckerFactory);
        Map<String, String> properties = new HashMap<>();
        properties.put(OCTOPUS_APIKEY, "API-KEY");
        properties.put(OCTOPUS_URL, "api key");
        Collection<InvalidProperty> result = processor.process(properties);

        Assert.assertEquals(result.size(), 0);
    }
}
