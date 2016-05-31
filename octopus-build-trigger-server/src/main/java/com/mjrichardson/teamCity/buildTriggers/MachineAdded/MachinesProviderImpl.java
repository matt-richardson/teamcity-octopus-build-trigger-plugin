package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.intellij.openapi.diagnostic.Logger;
import com.mjrichardson.teamCity.buildTriggers.*;
import com.mjrichardson.teamCity.buildTriggers.Exceptions.*;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class MachinesProviderImpl implements MachinesProvider {
    @NotNull
    private static final Logger LOG = Logger.getInstance(MachinesProviderImpl.class.getName());
    private final HttpContentProviderFactory httpContentProviderFactory;
    private final AnalyticsTracker analyticsTracker;

    public MachinesProviderImpl(HttpContentProviderFactory httpContentProviderFactory, AnalyticsTracker analyticsTracker) {
        this.httpContentProviderFactory = httpContentProviderFactory;
        this.analyticsTracker = analyticsTracker;
    }

    public Machines getMachines(UUID correlationId) throws InvalidOctopusApiKeyException, InvalidOctopusUrlException, MachinesProviderException {
        String url = null;

        try {
            HttpContentProvider contentProvider = httpContentProviderFactory.getContentProvider();
            url = contentProvider.getUrl();

            LOG.debug(String.format("%s: Getting machines from %s", correlationId, contentProvider.getUrl()));

            final ApiRootResponse apiRootResponse = getApiRootResponse(contentProvider, correlationId);
            return getMachines(contentProvider, apiRootResponse, correlationId);

        } catch (InvalidOctopusApiKeyException | InvalidOctopusUrlException e) {
            throw e;
        } catch (Throwable e) {
            throw new MachinesProviderException(String.format("Unexpected exception in MachinesProviderImpl, while attempting to get Machines from %s: %s", url, e), e);
        }
    }

    private Machines getMachines(HttpContentProvider contentProvider, ApiRootResponse apiRootResponse, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException, ProjectNotFoundException, InvalidCacheConfigurationException {
        String machinesResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiMachines, apiRootResponse.machinesApiLink, correlationId);
        ApiMachinesResponse apiMachinesResponse = new ApiMachinesResponse(machinesResponse);
        Machines machines = apiMachinesResponse.machines;
        while (shouldGetNextMachinesPage(apiMachinesResponse, machines)) {
            machinesResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiMachines, apiMachinesResponse.nextLink, correlationId);
            apiMachinesResponse = new ApiMachinesResponse(machinesResponse);
            Machines newMachines = apiMachinesResponse.machines;
            machines.add(newMachines);
        }
        return machines;
    }

    @NotNull
    private ApiRootResponse getApiRootResponse(HttpContentProvider contentProvider, UUID correlationId) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException, InvalidCacheConfigurationException {
        final String apiResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiRoot, "/api", correlationId);
        return new ApiRootResponse(apiResponse, analyticsTracker, correlationId);
    }

    private boolean shouldGetNextMachinesPage(ApiMachinesResponse apiMachinesResponse, Machines machines) {
        if (machines.isEmpty())
            return false;
        if (apiMachinesResponse.nextLink == null)
            return false;
        return true;
    }
}
