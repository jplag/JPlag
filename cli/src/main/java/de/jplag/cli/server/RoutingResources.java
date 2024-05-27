package de.jplag.cli.server;

import com.sun.net.httpserver.HttpExchange;

/**
 * Responds with data from the resources.
 */
public class RoutingResources implements Routing {
    private String prefix;

    /**
     * @param prefix The prefix to use within the resources
     */
    public RoutingResources(String prefix) {
        this.prefix = prefix;

        if (!this.prefix.startsWith("/")) {
            this.prefix = "/" + this.prefix;
        }

        if (!this.prefix.endsWith("/")) {
            this.prefix = this.prefix + "/";
        }
    }

    @Override
    public ResponseData fetchData(RoutingPath subPath, HttpExchange request, ReportViewer viewer) {
        String fullPath = this.prefix + subPath.asPath();
        return ResponseData.fromResourceUrl(fullPath);
    }
}
