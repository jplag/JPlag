package de.jplag.cli.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Manages the tree of paths handled by the web server.
 */
public class RoutingTree {
    private final RoutingTreeNode root;

    /**
     * Creates an empty tree.
     */
    public RoutingTree() {
        this.root = new RoutingTreeNode();
    }

    /**
     * Adds a new routing to the tree.
     * @param path The path to use the routing for
     * @param routing The routing
     */
    public void insertRouting(RoutingPath path, Routing routing) {
        this.root.buildRouting(path, routing);
    }

    /**
     * Adds a new routing to the tree.
     * @param path The path to use the routing for
     * @param routing The routing
     */
    public void insertRouting(String path, Routing routing) {
        this.insertRouting(new RoutingPath(path), routing);
    }

    /**
     * Gets the routing for a given path.
     * @param path The path to look up
     * @return The remaining path to be handled by the routing and the found routing
     */
    public Pair<RoutingPath, Routing> resolveRouting(RoutingPath path) {
        return this.root.resolve(path);
    }

    private static class RoutingTreeNode {
        private final Map<String, RoutingTreeNode> children;
        private Routing routing;

        RoutingTreeNode(RoutingPath building, Routing routing) {
            this();
            this.buildRouting(building, routing);
        }

        RoutingTreeNode() {
            this.children = new HashMap<>();
        }

        public void buildRouting(RoutingPath building, Routing routing) {
            if (building.isEmpty()) {
                this.routing = routing;
            } else if (this.children.containsKey(building.head())) {
                this.children.get(building.head()).buildRouting(building.tail(), routing);
            } else {
                this.children.put(building.head(), new RoutingTreeNode(building.tail(), routing));
            }
        }

        public Pair<RoutingPath, Routing> resolve(RoutingPath path) {
            if ((path.isEmpty() || !this.children.containsKey(path.head())) && this.routing != null) {
                return Pair.of(path, this.routing);
            }

            if (this.children.containsKey(path.head()) && !path.isEmpty()) {
                Pair<RoutingPath, Routing> childResolved = this.children.get(path.head()).resolve(path.tail());
                if (childResolved == null && this.routing != null) {
                    return Pair.of(path, this.routing);
                }
                return childResolved;
            }

            return null;
        }
    }
}
