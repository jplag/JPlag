package de.jplag.cli.server;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class RoutingFallbackTest {
    private final Routing nullRouting;
    private final Routing contentRouting;

    RoutingFallbackTest() throws IOException {
        File testFile = File.createTempFile("content", ".any");
        this.nullRouting = new RoutingStaticFile(null, ContentType.PLAIN);
        this.contentRouting = new RoutingStaticFile(testFile, ContentType.PLAIN);
    }

    @Test
    void testSecondNull() {
        Routing routing = this.nullRouting.or(this.contentRouting);
        assertNotNull(routing.fetchData(null, null, null));
    }

    @Test
    void testFirstNull() {
        Routing routing = this.contentRouting.or(this.nullRouting);
        assertNotNull(routing.fetchData(null, null, null));
    }

    @Test
    void testNeitherNull() {
        Routing routing = this.contentRouting.or(this.contentRouting);
        assertNotNull(routing.fetchData(null, null, null));
    }

    @Test
    void testBothNull() {
        Routing routing = this.nullRouting.or(this.nullRouting);
        assertNull(routing.fetchData(null, null, null));
    }
}