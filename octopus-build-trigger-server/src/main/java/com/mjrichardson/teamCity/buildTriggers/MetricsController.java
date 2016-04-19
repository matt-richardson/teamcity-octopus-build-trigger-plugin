package com.mjrichardson.teamCity.buildTriggers;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

public class MetricsController extends BaseController {

    private final ObjectMapper objectMapper;
    private final WebControllerManager webManager;
    private final MetricRegistry metricRegistry;
    private static final Logger LOG = Logger.getInstance(ProjectsController.class.getName());

    public MetricsController(SBuildServer server,
                             ObjectMapper objectMapper,
                             WebControllerManager webManager,
                             MetricRegistry metricRegistry) {
        super(server);
        this.objectMapper = objectMapper;
        this.webManager = webManager;
        this.metricRegistry = metricRegistry;
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) throws Exception {
        httpServletResponse.setHeader("content-type", "application/json");
        objectMapper.writeValue(httpServletResponse.getOutputStream(), metricRegistry);
        return null;
    }

    public void register(){
        LOG.info("MetricsController.Register() called");
        objectMapper.registerModules(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, false));
        webManager.registerController("/octopus-build-trigger/metrics.html", this);
    }
}
