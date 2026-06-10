package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.jplag.cli.picocli.CliInputHandler;
import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;

class HostOptionTest extends CliTest {
    @Test
    void testDefaultHost() throws IOException, ExitException {
        CliInputHandler inputHandler = this.runCli().inputHandler();
        assertEquals("127.0.0.1", inputHandler.getCliOptions().advanced.host);
    }

    @Test
    void testCustomHost() throws IOException, ExitException {
        CliInputHandler inputHandler = this.runCli(args -> args.with(CliArgument.HOST, "0.0.0.0")).inputHandler();
        assertEquals("0.0.0.0", inputHandler.getCliOptions().advanced.host);
    }

    @Test
    void testLocalhostHostname() throws IOException, ExitException {
        CliInputHandler inputHandler = this.runCli(args -> args.with(CliArgument.HOST, "localhost")).inputHandler();
        assertEquals("localhost", inputHandler.getCliOptions().advanced.host);
    }

    @Test
    void testIPv6Loopback() throws IOException, ExitException {
        CliInputHandler inputHandler = this.runCli(args -> args.with(CliArgument.HOST, "::1")).inputHandler();
        assertEquals("::1", inputHandler.getCliOptions().advanced.host);
    }

    @Test
    void testInvalidHost() {
        assertThrowsExactly(CliException.class, () -> {
            this.runCli(args -> args.with(CliArgument.HOST, "invalid.host.address.that.does.not.exist"));
        });
    }

    @Test
    void testInvalidHostMessage() {
        CliException exception = assertThrowsExactly(CliException.class, () -> {
            this.runCli(args -> args.with(CliArgument.HOST, "invalid.host"));
        });
        assertTrue(exception.getMessage().contains("Invalid bind address: invalid.host"));
    }

    @Override
    public void addDefaultParameters() {
        // prevents the submission directory from being added to the parameters automatically
    }
}
