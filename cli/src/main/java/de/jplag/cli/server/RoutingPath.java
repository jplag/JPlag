package de.jplag.cli.server;

import java.util.Arrays;

/**
 * A path used for routing. Can be used like a linked list.
 */
public class RoutingPath {
    private final String[] components;
    private final int offset;

    /**
     * @param path The full path
     */
    public RoutingPath(String path) {
        this.components = Arrays.stream(path.split("/", 0)).filter(it -> !it.isBlank()).toArray(String[]::new);
        this.offset = 0;
    }

    private RoutingPath(String[] components, int offset) {
        this.components = components;
        this.offset = offset;
    }

    /**
     * @return The first path segment
     */
    public String head() {
        return components[offset];
    }

    /**
     * Returns a new RoutingPath representing the tail (next segment) of the current path.
     * @return the tail RoutingPath
     * @throws IllegalStateException if there is no tail (path is complete)
     */
    public RoutingPath tail() {
        if (!hasTail()) {
            throw new IllegalStateException("Routing path is done.");
        }

        return new RoutingPath(this.components, this.offset + 1);
    }

    /**
     * @return True, if the tail has at least 0 elements
     */
    public boolean hasTail() {
        return this.components.length > this.offset;
    }

    /**
     * @return True, if there are no segments in this path
     */
    public boolean isEmpty() {
        return this.offset == this.components.length;
    }

    /**
     * @return The remaining path as a string
     */
    public String asPath() {
        StringBuilder builder = new StringBuilder();

        for (int i = this.offset; i < this.components.length; i++) {
            if (i > this.offset) {
                builder.append("/");
            }
            builder.append(this.components[i]);
        }

        return builder.toString();
    }
}
