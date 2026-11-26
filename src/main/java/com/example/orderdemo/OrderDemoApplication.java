package com.example.orderdemo;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import java.net.URL;

/**
 * Embedded Jetty entry point for the legacy-order-demo web application.
 */
public class OrderDemoApplication {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        int port = resolvePort();
        Server server = createServer(port);
        server.start();
        server.join();
    }

    /**
     * Builds and configures the embedded Jetty server with servlets and static resources.
     *
     * @param port TCP port to listen on (0 selects an ephemeral port)
     * @return configured Jetty {@link Server} instance
     */
    public static Server createServer(int port) {
        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        URL webRoot = OrderDemoApplication.class.getClassLoader().getResource("webapp");
        if (webRoot == null) {
            throw new IllegalStateException("Could not locate static webapp resources on classpath.");
        }
        context.setBaseResource(Resource.newResource(webRoot));
        context.setWelcomeFiles(new String[]{"index.html"});

        context.addServlet(new ServletHolder(new HealthServlet()), "/health");
        context.addServlet(new ServletHolder(new OrderCalculateServlet()), "/api/orders/calculate");
        context.addServlet(new ServletHolder("default", DefaultServlet.class), "/");

        server.setHandler(context);
        return server;
    }

    private static int resolvePort() {
        String sysProp = System.getProperty("server.port");
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return Integer.parseInt(sysProp.trim());
        }
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.trim().isEmpty()) {
            return Integer.parseInt(envPort.trim());
        }
        return DEFAULT_PORT;
    }
}
