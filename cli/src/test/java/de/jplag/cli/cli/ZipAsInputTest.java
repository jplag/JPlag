package de.jplag.cli.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class ZipAsInputTest extends CliTest {
    private static Path testZip;

    @BeforeAll
    static void setUp() throws IOException {
        testZip = Files.createTempFile("test", ".zip");
        try (var _ = new ZipOutputStream(Files.newOutputStream(testZip))) {
            // Creates an empty zip
        }
    }

    @AfterAll
    static void tearDown() throws IOException {
        Files.delete(testZip);
    }

    @Test
    void singleZipInput() throws IOException, ExitException {
        JPlagOptions options = runCliForOptions(builder -> builder.with(CliArgument.SUBMISSION_DIRECTORIES, new String[] {testZip.toString()}));
        assertEquals(1, options.submissionDirectories().size());
        assertEquals("jar", options.submissionDirectories().iterator().next().getFileSystem().provider().getScheme());
    }

    @Override
    public void addDefaultParameters() {
        // Prevent default parameters
    }
}
