package de.jplag.cli.server;

import com.sun.net.httpserver.HttpExchange;

public class RoutingFallback implements Routing {
    private final Routing first;
    private final Routing second;

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
