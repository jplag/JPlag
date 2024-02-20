package de.jplag.cli.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RoutingStaticFileTest {
    private static final String TEST_FILE_CONTENT = "some test content.";
    private static final ContentType TEST_CONTENT_TYPE = ContentType.PLAIN;
    private static RoutingStaticFile routing;

    @BeforeAll
    static void setUp() throws IOException {
        File testFile = File.createTempFile("testFile", ".txt");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(TEST_FILE_CONTENT);
        }
        routing = new RoutingStaticFile(testFile, TEST_CONTENT_TYPE);
    }

    @Test
    void testRespondsWithFileContent() throws IOException {
        ResponseData responseData = routing.fetchData(null, null, null);
        assertEquals(TEST_CONTENT_TYPE, responseData.contentType());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseData.stream()))) {
            assertEquals(TEST_FILE_CONTENT, reader.readLine());
        }
    }

    @Test
    void testWithNullFile() throws IOException {
        RoutingStaticFile nullRouting = new RoutingStaticFile(null, TEST_CONTENT_TYPE);
        assertNull(nullRouting.fetchData(null, null, null));
    }
}