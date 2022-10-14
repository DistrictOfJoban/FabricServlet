package com.lx.fabricservlet;

import com.lx.fabricservlet.Config.config;
import com.lx.fabricservlet.Data.FabricServletServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FabricServlet implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("FabricServlet");
    public static Version versions = null;
    public static ArrayList<FabricServletServer> serverList = new ArrayList<>();

    @Override
    public void onInitialize() {
        versions = FabricLoader.getInstance().getModContainer("fbweb").get().getMetadata().getVersion();
        LOGGER.info("[FabricServlet] Version " + String.valueOf(versions));
        config.loadConfig();
        startAllServers();
    }

    public static void startAllServers() {

        for(FabricServletServer servers : serverList) {
            LOGGER.info("[FabricServlet] Initializing webserver " + servers.id++);
            final Server webServer = new Server(new QueuedThreadPool(100, 10, 160));
            final ServerConnector serverConnector = new ServerConnector(webServer);
            webServer.setConnectors(new Connector[]{serverConnector});
            final ServletContextHandler context = new ServletContextHandler();
            webServer.setHandler(context);
            URL url = null;

            try {
                url = new URL(servers.path);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (url != null) {
                try {
                    context.setBaseResource(Resource.newResource(url.toURI()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            final ServletHolder servletHolder = new ServletHolder("default", DefaultServlet.class);
            servletHolder.setInitParameter("dirAllowed", String.valueOf(servers.dirListing));
            context.addServlet(servletHolder, "/");

            serverConnector.setPort(servers.port);
            servers.activeServer = webServer;
            try {
                webServer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void stopAllServers() {
        for(FabricServletServer server : serverList) {
            LOGGER.info("[FabricServlet] Stopping webserver " + server.id++);
            try {
                if(server.activeServer != null) {
                    server.activeServer.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
