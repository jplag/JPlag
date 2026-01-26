package de.jplag.cli.server;

import com.sun.net.httpserver.HttpExchange;

/**
 * Handles the data for an url prefix.
 */
public interface Routing {
    /**
     * @return The methods, that this routing can be used for.
     */
    default HttpRequestMethod[] allowedMethods() {
        return new HttpRequestMethod[] {HttpRequestMethod.GET};
    }

    /**
     * Gets the data for the given url.
     * @param subPath The remaining suffix of the url, that is not yet interpreted
     * @param request The original http request
     * @param viewer The current report viewer
     * @return The data to respond with
     */
    ResponseData fetchData(RoutingPath subPath, HttpExchange request, ReportViewer viewer);

    /**
     * Use the other routing if this routing does not find any data.
     * @param other The other routing
     * @return The combined routing
     */
    default Routing or(Routing other) {
        return new RoutingFallback(this, other);
    }
}
