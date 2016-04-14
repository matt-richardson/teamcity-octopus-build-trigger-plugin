package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.*;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class MachinesProviderImpl implements MachinesProvider {
    @NotNull
    private static final Logger LOG = Logger.getInstance(MachinesProviderImpl.class.getName());
    private final HttpContentProviderFactory httpContentProviderFactory;
    private final AnalyticsTracker analyticsTracker;

    public MachinesProviderImpl(HttpContentProviderFactory httpContentProviderFactory, AnalyticsTracker analyticsTracker) {
        this.httpContentProviderFactory = httpContentProviderFactory;
        this.analyticsTracker = analyticsTracker;
    }

    public Machines getMachines() throws InvalidOctopusApiKeyException, InvalidOctopusUrlException, MachinesProviderException {
        String url = null;

        try {
            HttpContentProvider contentProvider = httpContentProviderFactory.getContentProvider();
            url = contentProvider.getUrl();

            LOG.debug("Getting machines from " + contentProvider.getUrl());

            final ApiRootResponse apiRootResponse = getApiRootResponse(contentProvider);
            return getMachines(contentProvider, apiRootResponse);

        } catch (InvalidOctopusApiKeyException | InvalidOctopusUrlException e) {
            throw e;
        } catch (Throwable e) {
            throw new MachinesProviderException(String.format("Unexpected exception in MachinesProviderImpl, while attempting to get Machines from %s: %s", url, e), e);
        }
    }

    private Machines getMachines(HttpContentProvider contentProvider, ApiRootResponse apiRootResponse) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException, ProjectNotFoundException, InvalidCacheConfigurationException {
        String machinesResponse = contentProvider.getContent(CacheManager.CacheNames.ApiMachines, apiRootResponse.machinesApiLink);
        ApiMachinesResponse apiMachinesResponse = new ApiMachinesResponse(machinesResponse);
        Machines machines = apiMachinesResponse.machines;
        while (shouldGetNextMachinesPage(apiMachinesResponse, machines)) {
            machinesResponse = contentProvider.getContent(CacheManager.CacheNames.ApiMachines, apiMachinesResponse.nextLink);
            apiMachinesResponse = new ApiMachinesResponse(machinesResponse);
            Machines newMachines = apiMachinesResponse.machines;
            machines.add(newMachines);
        }
        return machines;
    }

    @NotNull
    private ApiRootResponse getApiRootResponse(HttpContentProvider contentProvider) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException, InvalidCacheConfigurationException {
        final String apiResponse = contentProvider.getContent(CacheManager.CacheNames.ApiRoot, "/api");
        return new ApiRootResponse(apiResponse, analyticsTracker);
    }

    private boolean shouldGetNextMachinesPage(ApiMachinesResponse apiMachinesResponse, Machines machines) {
        if (machines.isEmpty())
            return false;
        if (apiMachinesResponse.nextLink == null)
            return false;
        return true;
    }
}
