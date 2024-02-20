package de.jplag.cli.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RoutingPathTest {
    private static final String TEST_PATH = "some/path/to/index.html";
    private static final String TEST_PATH_WITH_BEGINNING_SLASH = "/some/path/to/index.html";
    private static final String TEST_PATH_WITH_ADDITIONAL_SLASHES = "///some/path////to/index.html";

    private static final String[] TEST_PATH_PARTS = new String[] {"some", "path", "to", "index.html"};

    @ParameterizedTest
    @ValueSource(strings = {TEST_PATH_WITH_BEGINNING_SLASH, TEST_PATH, TEST_PATH_WITH_ADDITIONAL_SLASHES})
    void testAsPath(String path) {
        RoutingPath routingPath = new RoutingPath(path);
        assertEquals(TEST_PATH, routingPath.asPath());
    }

    @Test
    void testIterating() {
        RoutingPath routingPath = new RoutingPath(TEST_PATH);
        for (String expectedPart : TEST_PATH_PARTS) {
            String currentPart = routingPath.head();
            routingPath = routingPath.tail();
            assertEquals(expectedPart, currentPart);
        }

        assertFalse(routingPath.hasTail());
        assertTrue(routingPath.isEmpty());
    }

    @Test
    void testErrorWithEmptyTail() {
        assertThrowsExactly(IllegalStateException.class, () -> {
            RoutingPath routingPath = new RoutingPath("");
            routingPath.tail();
        });
    }
}