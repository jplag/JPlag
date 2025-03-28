package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class DebugTest extends CliTest {
    @Test
    void testDefaultDebug() throws IOException, ExitException {
        JPlagOptions options = runCliForOptions();
        assertFalse(options.debugParser());
    }

    @Test
    void testSetDebug() throws IOException, ExitException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.DEBUG, true));
        assertTrue(options.debugParser());
    }
}
