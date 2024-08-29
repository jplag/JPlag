package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;

class LogLevelTest extends CliTest {
    private static final Level DEFAULT_LOG_LEVEL = Level.INFO;

    @Test
    void testDefaultLogLevel() throws IOException, ExitException {
        Level level = runCliForLogLevel();
        assertEquals(DEFAULT_LOG_LEVEL, level);
    }

    @Test
    void testSetLogLevel() throws IOException, ExitException {
        Level level = runCliForLogLevel(args -> args.with(CliArgument.LOG_LEVEL, Level.ERROR.name()));
        assertEquals(Level.ERROR, level);
    }
}
