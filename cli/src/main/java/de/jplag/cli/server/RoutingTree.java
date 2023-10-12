package de.jplag.cli.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class RoutingTree {
    private final RoutingTreeNode root;

    public RoutingTree() {
        this.root = new RoutingTreeNode();
    }

    public void insertRouting(RoutingPath path, Routing routing) {
        this.root.buildRouting(path, routing);
    }

    public void insertRouting(String path, Routing routing) {
        this.insertRouting(new RoutingPath(path), routing);
    }

    public Pair<RoutingPath, Routing> resolveRouting(RoutingPath path) {
        return this.root.resolve(path);
    }

    private static class RoutingTreeNode {
        private final Map<String, RoutingTreeNode> children;
        private Routing routing;

        public RoutingTreeNode(RoutingPath building, Routing routing) {
            this();
            this.buildRouting(building, routing);
        }

        public RoutingTreeNode() {
            this.children = new HashMap<>();
        }

        public void buildRouting(RoutingPath building, Routing routing) {
            if (building.isEmpty()) {
                this.routing = routing;
            } else {
                if (this.children.containsKey(building.head())) {
                    this.children.get(building.head()).buildRouting(building.tail(), routing);
                } else {
                    this.children.put(building.head(), new RoutingTreeNode(building.tail(), routing));
                }
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
                } else {
                    return childResolved;
                }
            }

            return null;
        }
    }
}
