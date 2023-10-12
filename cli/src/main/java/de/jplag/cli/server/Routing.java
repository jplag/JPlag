package de.jplag.cli.server;

import java.io.InputStream;

import org.apache.commons.lang3.tuple.Pair;

import com.sun.net.httpserver.HttpExchange;

public interface Routing {
    default HttpMethod[] allowedMethods() {
        return new HttpMethod[] {HttpMethod.GET};
    }

    Pair<InputStream, ContentType> fetchData(RoutingPath subPath, HttpExchange request, ReportViewer viewer);
}
