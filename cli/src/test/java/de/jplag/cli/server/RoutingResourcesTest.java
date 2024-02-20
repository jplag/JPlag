package de.jplag.cli.server;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class RoutingResourcesTest {
    private static final RoutingResources routing = new RoutingResources("/");

    @Test
    void testExistingFile() {
        assertNotNull(routing.fetchData(new RoutingPath("testResource.txt"), null, null));
    }

    @Test
    void testNotExistingFile() {
        assertNull(routing.fetchData(new RoutingPath("otherFile.txt"), null, null));
    }
}