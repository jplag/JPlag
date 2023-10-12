package de.jplag.cli.server;

import java.io.InputStream;

import org.apache.commons.lang3.tuple.Pair;

import com.sun.net.httpserver.HttpExchange;

public class RoutingResources implements Routing {
    private String prefix;

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
    public Pair<InputStream, ContentType> fetchData(RoutingPath subPath, HttpExchange request, ReportViewer viewer) {
        String fullPath = this.prefix + subPath.asPath();
        InputStream stream = this.getClass().getResourceAsStream(fullPath);

        if (stream == null) {
            return null;
        }

        return Pair.of(stream, null);
    }
}
