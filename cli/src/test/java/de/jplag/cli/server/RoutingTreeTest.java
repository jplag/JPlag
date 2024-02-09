package de.jplag.cli.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpExchange;

class RoutingTreeTest {
    private static final String firstRoutingPath = "/content/image.png";
    private static final String secondRoutingPath = "/index.html";
    private RoutingTree routingTree;

    @BeforeEach
    void setUp() {
        this.routingTree = new RoutingTree();
        this.routingTree.insertRouting(firstRoutingPath, new TestRouting(firstRoutingPath));
        this.routingTree.insertRouting(secondRoutingPath, new TestRouting(secondRoutingPath));
    }

    @Test
    public void testAccessRoutingTree() {
        Pair<RoutingPath, Routing> firstRouting = this.routingTree.resolveRouting(new RoutingPath(firstRoutingPath));
        Pair<RoutingPath, Routing> secondRouting = this.routingTree.resolveRouting(new RoutingPath(secondRoutingPath + "/suffix"));

        assertTrue(firstRouting.getLeft().isEmpty());
        assertFalse(secondRouting.getLeft().isEmpty());
        assertEquals("suffix", secondRouting.getLeft().asPath());

        assertInstanceOf(TestRouting.class, firstRouting.getRight());
        assertInstanceOf(TestRouting.class, secondRouting.getRight());

        assertEquals(firstRoutingPath, ((TestRouting) firstRouting.getRight()).path);
        assertEquals(secondRoutingPath, ((TestRouting) secondRouting.getRight()).path);
    }

    private static class TestRouting implements Routing {
        private final String path;

        public TestRouting(String path) {
            this.path = path;
        }

        @Override
        public ResponseData fetchData(RoutingPath subPath, HttpExchange request, ReportViewer viewer) {
            return null;
        }
    }
}