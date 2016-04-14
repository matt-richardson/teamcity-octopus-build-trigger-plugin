package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentCompleteAsyncBuildTrigger;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentCompleteSpec;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeServiceLocator;
import jetbrains.buildServer.ServiceLocator;
import jetbrains.buildServer.buildTriggers.PolledBuildTrigger;
import jetbrains.buildServer.buildTriggers.async.JobStatusStorageHolder;
import jetbrains.buildServer.buildTriggers.async.impl.JobStatusStorageHolderImpl;
import jetbrains.buildServer.serverSide.executors.ExecutorServices;
import jetbrains.buildServer.serverSide.impl.executors.SimpleExecutorServices;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

@Test
public class CustomAsyncBuildTriggerFactoryImplTest {
    public void create_build_trigger_returns_custom_async_polled_build_trigger() throws IOException {
        ExecutorServices executorServices = new SimpleExecutorServices();
        JobStatusStorageHolder jobStatusStorageHolder = new JobStatusStorageHolderImpl();
        ServiceLocator serviceLocator = new FakeServiceLocator();
        CustomAsyncBuildTriggerFactoryImpl factory = new CustomAsyncBuildTriggerFactoryImpl(executorServices, jobStatusStorageHolder, serviceLocator);
        Class clazz = DeploymentCompleteSpec.class;

        AnalyticsTracker analyticsTracker = new FakeAnalyticsTracker();
        String displayName = "display-name";
        int pollInterval = OctopusBuildTriggerUtil.getPollInterval();
        CustomAsyncBuildTrigger<DeploymentCompleteSpec> trigger = new DeploymentCompleteAsyncBuildTrigger(displayName, pollInterval, analyticsTracker, new FakeCacheManager());
        Logger logger = Logger.getInstance(DeploymentCompleteAsyncBuildTrigger.class.getName());
        Integer invocationInterval = 60 * 1000;

        PolledBuildTrigger result = factory.createBuildTrigger(clazz, trigger, logger, invocationInterval);
        Assert.assertNotNull(result);

        CustomAsyncPolledBuildTrigger<DeploymentCompleteSpec> resultAsCustomTrigger = (CustomAsyncPolledBuildTrigger<DeploymentCompleteSpec>) result;
        Assert.assertEquals(resultAsCustomTrigger.log, logger);
        Assert.assertEquals(resultAsCustomTrigger.asyncBuildTrigger, trigger);
        Assert.assertEquals(resultAsCustomTrigger.serviceLocator, serviceLocator);
    }
}
