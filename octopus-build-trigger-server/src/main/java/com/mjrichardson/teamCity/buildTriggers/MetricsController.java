package com.mjrichardson.teamCity.buildTriggers;

import com.codahale.metrics.MetricRegistry;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MetricsController extends BaseController {

    private final WebControllerManager webManager;
    private static final MetricRegistry metrics = new MetricRegistry();
    private static final Logger LOG = Logger.getInstance(ProjectsController.class.getName());

    public MetricsController(SBuildServer server,
                             WebControllerManager webManager) {
        super(server);
        this.webManager = webManager;
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) throws Exception {
        //new com.codahale.metrics.servlets.AdminServlet().service(httpServletRequest, httpServletResponse);
        //return null;
        RequestDispatcher rd = httpServletRequest.getRequestDispatcher("servlet2");
        rd.forward(httpServletRequest,httpServletResponse);
        return null;
    }

    public void register(){
        LOG.info("MetricsController.Register() called");
        webManager.registerController("/octopus-build-trigger/metrics.html", this);
    }
}
