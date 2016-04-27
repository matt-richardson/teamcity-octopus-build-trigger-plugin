package com.mjrichardson.teamCity.buildTriggers;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.parser.ParseException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class ProjectsController extends BaseController {
    private final WebControllerManager webManager;
    private final AnalyticsTracker analyticsTracker;
    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;
    private static final Logger LOG = Logger.getInstance(ProjectsController.class.getName());
    private MetricRegistry metricRegistry;

    public ProjectsController(SBuildServer server,
                              WebControllerManager webManager,
                              AnalyticsTracker analyticsTracker,
                              ObjectMapper objectMapper,
                              CacheManager cacheManager,
                              MetricRegistry metricRegistry) {
        super(server);
        this.webManager = webManager;
        this.analyticsTracker = analyticsTracker;
        this.objectMapper = objectMapper;
        this.cacheManager = cacheManager;
        this.metricRegistry = metricRegistry;
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) throws Exception {
        LOG.info("ProjectsController.doHandle() called");
        String octopusUrl = httpServletRequest.getParameter("octopusUrl");
        String octopusApiKey = httpServletRequest.getParameter("octopusApiKey");
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(octopusUrl, octopusApiKey,
                OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds(), cacheManager, metricRegistry);
        HttpContentProvider contentProvider = contentProviderFactory.getContentProvider();

        httpServletResponse.setHeader("content-type", "application/json");
        try {
            LOG.debug("Getting projects from " + contentProvider.getUrl());
            final ApiRootResponse apiRootResponse = getApiRootResponse(contentProvider);
            final Projects projects = getProjects(contentProvider, apiRootResponse);
            objectMapper.writeValue(httpServletResponse.getOutputStream(), projects.toArray());
        }
        catch (InvalidOctopusApiKeyException ex) {
            LOG.warn("Invalid octopus api key exception, connecting to " + octopusUrl);
            objectMapper.writeValue(httpServletResponse.getOutputStream(), new ErrorMessage("ApiKey", "Invalid API Key"));
        }
        catch (InvalidOctopusUrlException ex) {
            LOG.warn("Invalid octopus url exception, connecting to " + octopusUrl);
            objectMapper.writeValue(httpServletResponse.getOutputStream(), new ErrorMessage("Url", "Invalid Octopus Url"));
        }
        catch (UnexpectedResponseCodeException ex) {
            LOG.warn("Unexpected response code exception, connecting to " + octopusUrl, ex);
            objectMapper.writeValue(httpServletResponse.getOutputStream(), new ErrorMessage("Url", "Unexpected response code - " + ex.code));
        }
        catch (Exception ex) {
            LOG.warn("Unexpected exception, connecting to " + octopusUrl, ex);
            objectMapper.writeValue(httpServletResponse.getOutputStream(), new ErrorMessage("Url", "Unexpected exception"));
        }

        return newEmptyModelAndView();
    }

    private ModelAndView newEmptyModelAndView() {
        return null;
    }

    public void register(){
        LOG.info("ProjectsController.Register() called");
        webManager.registerController("/octopus-build-trigger/projects.html", this);
    }

    @NotNull
    private ApiRootResponse getApiRootResponse(HttpContentProvider contentProvider) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException, InvalidCacheConfigurationException {
        final String apiResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiRoot, "/api");
        return new ApiRootResponse(apiResponse, analyticsTracker);
    }

    //todo: de-dupe
    private Projects getProjects(HttpContentProvider contentProvider, ApiRootResponse apiRootResponse) throws IOException, UnexpectedResponseCodeException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, URISyntaxException, ProjectNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException, InvalidCacheConfigurationException {
        String projectsResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiProjects, apiRootResponse.projectsApiLink);
        ApiProjectsResponse apiProjectsResponse = new ApiProjectsResponse(projectsResponse);
        Projects projects = apiProjectsResponse.projects;
        while (shouldGetNextProjectsPage(apiProjectsResponse)) {
            projectsResponse = contentProvider.getOctopusContent(CacheManager.CacheNames.ApiProjects, apiProjectsResponse.nextLink);
            apiProjectsResponse = new ApiProjectsResponse(projectsResponse);
            Projects newProjects = apiProjectsResponse.projects;
            projects.add(newProjects);
        }
        return projects;
    }

    private boolean shouldGetNextProjectsPage(ApiProjectsResponse apiProjectsResponse) {
        if (apiProjectsResponse.nextLink == null)
            return false;
        return true;
    }

    private class ErrorMessage {
        public String message;
        public String type;

        public ErrorMessage(String type, String message) {
            this.type = type;
            this.message = message;
        }
    }
}
