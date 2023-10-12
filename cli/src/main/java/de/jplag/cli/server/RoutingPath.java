package de.jplag.cli.server;

import java.util.Arrays;

public class RoutingPath {
    private final String[] components;
    private final int offset;

    public RoutingPath(String path) {
        this.components = Arrays.stream(path.split("/", 0)).filter(it -> !it.isBlank()).toArray(String[]::new);
        this.offset = 0;
    }

    private RoutingPath(String[] components, int offset) {
        this.components = components;
        this.offset = offset;
    }

    public String head() {
        return components[offset];
    }

    public RoutingPath tail() {
        if (!hasTail()) {
            throw new IllegalStateException("Routing path is done.");
        }

        return new RoutingPath(this.components, this.offset + 1);
    }

    public boolean hasTail() {
        return this.components.length > this.offset;
    }

    public boolean isEmpty() {
        return this.offset == this.components.length;
    }

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
