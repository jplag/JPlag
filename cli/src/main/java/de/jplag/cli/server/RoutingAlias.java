package de.jplag.cli.server;

import org.apache.commons.lang3.tuple.Pair;

import com.sun.net.httpserver.HttpExchange;

/**
 * An alias routing, that will respond with the response for a different path.
 */
public class RoutingAlias implements Routing {
    private final RoutingPath path;

    /**
     * @param path The path to actually use.
     */
    public RoutingAlias(RoutingPath path) {
        this.path = path;
    }

    /**
     * @param path The path to actually use.
     */
    public RoutingAlias(String path) {
        this(new RoutingPath(path));
    }

    @Override
    public ResponseData fetchData(RoutingPath subPath, HttpExchange request, ReportViewer viewer) {
        Pair<RoutingPath, Routing> redirect = viewer.getRoutingTree().resolveRouting(path);
        if (redirect == null) {
            return null;
        }

        return redirect.getValue().fetchData(redirect.getLeft(), request, viewer);
    }
}
