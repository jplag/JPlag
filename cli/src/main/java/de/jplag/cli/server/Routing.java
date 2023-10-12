package de.jplag.cli.server;

import com.sun.net.httpserver.HttpExchange;

public interface Routing {
    default HttpMethod[] allowedMethods() {
        return new HttpMethod[] {HttpMethod.GET};
    }

    ResponseData fetchData(RoutingPath subPath, HttpExchange request, ReportViewer viewer);

    default Routing or(Routing other) {
        return new RoutingFallback(this, other);
    }
}
