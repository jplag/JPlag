package de.jplag.cli.server;

import org.apache.commons.lang3.tuple.Pair;

import com.sun.net.httpserver.HttpExchange;

public class RoutingRedirect implements Routing {
    private final RoutingPath path;

    public RoutingRedirect(RoutingPath path) {
        this.path = path;
    }

    public RoutingRedirect(String path) {
        this(new RoutingPath(path));
    }

    @Override
    public ResponseData fetchData(RoutingPath subPath, HttpExchange request, ReportViewer viewer) {
        if (subPath.hasTail()) {
            return null;
        }

        Pair<RoutingPath, Routing> redirect = viewer.getRoutingTree().resolveRouting(path);
        if (redirect == null) {
            return null;
        }

        return redirect.getValue().fetchData(redirect.getLeft(), request, viewer);
    }
}
