package de.jplag.cli.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpExchange;

class RoutingTreeTest {
    private static final String FIRST_ROUTING_PATH = "/content/image.png";
    private static final String SECOND_ROUTING_PATH = "/index.html";
    private RoutingTree routingTree;

    @BeforeEach
    void setUp() {
        this.routingTree = new RoutingTree();
        this.routingTree.insertRouting(FIRST_ROUTING_PATH, new TestRouting(FIRST_ROUTING_PATH));
        this.routingTree.insertRouting(SECOND_ROUTING_PATH, new TestRouting(SECOND_ROUTING_PATH));
    }

    @Test
    void testAccessRoutingTree() {
        Pair<RoutingPath, Routing> firstRouting = this.routingTree.resolveRouting(new RoutingPath(FIRST_ROUTING_PATH));
        Pair<RoutingPath, Routing> secondRouting = this.routingTree.resolveRouting(new RoutingPath(SECOND_ROUTING_PATH + "/suffix"));

        assertTrue(firstRouting.getLeft().isEmpty());
        assertFalse(secondRouting.getLeft().isEmpty());
        assertEquals("suffix", secondRouting.getLeft().asPath());

        assertInstanceOf(TestRouting.class, firstRouting.getRight());
        assertInstanceOf(TestRouting.class, secondRouting.getRight());

        assertEquals(FIRST_ROUTING_PATH, ((TestRouting) firstRouting.getRight()).path);
        assertEquals(SECOND_ROUTING_PATH, ((TestRouting) secondRouting.getRight()).path);
    }

    @Test
    void testUnknownPath() {
        assertNull(this.routingTree.resolveRouting(new RoutingPath("/unknown.html")));
    }

    @Test
    void testPartialPathRoute() {
        RoutingTree routingTree = new RoutingTree();
        routingTree.insertRouting("/path/", new TestRouting(""));
        assertNotNull(routingTree.resolveRouting(new RoutingPath("/path/index.html")));
    }

    @Test
    void testPartialPathRouteWithSubPath() {
        RoutingTree routingTree = new RoutingTree();
        routingTree.insertRouting("/path/", new TestRouting("/path/"));
        routingTree.insertRouting("/path/subPath/a.html", new TestRouting(""));

        Pair<RoutingPath, Routing> result = routingTree.resolveRouting(new RoutingPath("/path/subPath/b.html"));
        assertNotNull(result);
        assertInstanceOf(TestRouting.class, result.getRight());
        assertEquals("/path/", ((TestRouting) result.getRight()).path);
    }

    private record TestRouting(String path) implements Routing {
        @Override
        public ResponseData fetchData(RoutingPath subPath, HttpExchange request, ReportViewer viewer) {
            return null;
        }
    }
}