package de.jplag.cli.server;

import com.sun.net.httpserver.HttpExchange;

/**
 * Responds with the first given routing, unless that would respond with null, in that case the second one is used.
 */
public class RoutingFallback implements Routing {
    private final Routing first;
    private final Routing second;

    /**
     * @param first The first routing
     * @param second The second routing
     */
    public RoutingFallback(Routing first, Routing second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public ResponseData fetchData(RoutingPath subPath, HttpExchange request, ReportViewer viewer) {
        ResponseData attempt = this.first.fetchData(subPath, request, viewer);
        if (attempt != null) {
            return attempt;
        }

        return this.second.fetchData(subPath, request, viewer);
    }
}
