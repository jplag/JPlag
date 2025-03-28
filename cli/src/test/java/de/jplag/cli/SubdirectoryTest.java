package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class SubdirectoryTest extends CliTest {
    private static final String TEST_SUBDIRECTORY = "dir";

    @Test
    void testDefaultSubdirectory() throws IOException, ExitException {
        JPlagOptions options = runCliForOptions();
        assertNull(options.subdirectoryName());
    }

    @Test
    void testSetSubdirectory() throws IOException, ExitException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SUBDIRECTORY, TEST_SUBDIRECTORY));
        assertEquals(TEST_SUBDIRECTORY, options.subdirectoryName());
    }
}
