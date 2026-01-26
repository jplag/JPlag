package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class ExcludeFileTest extends CliTest {
    private static final String EXCLUDE_FILE_NAME = "exclusions";

    @Test
    void testNoDefaultExcludeFile() throws IOException, ExitException {
        JPlagOptions options = runCliForOptions();
        assertNull(options.exclusionFileName());
    }

    @Test
    void testSetExcludeFile() throws IOException, ExitException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.EXCLUDE_FILES, EXCLUDE_FILE_NAME));
        assertEquals(EXCLUDE_FILE_NAME, options.exclusionFileName());
    }
}
