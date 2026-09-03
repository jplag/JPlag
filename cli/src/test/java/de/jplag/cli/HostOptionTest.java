package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetAddress;

import org.junit.jupiter.api.Test;

import de.jplag.cli.picocli.CliInputHandler;
import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;

class HostOptionTest extends CliTest {
    private static final String ANY_HOST = "0.0.0.0";
    private static final String LOCALHOST = "localhost";
    private static final String IPV6_LOOPBACK = "::1";
    private static final String INVALID_HOST = "invalid.host";
    private static final String INVALID_HOST_WITH_DOMAIN = "invalid.host.address.that.does.not.exist";

    @Test
    void testDefaultHost() throws IOException, ExitException {
        CliInputHandler inputHandler = this.runCli().inputHandler();
        assertEquals(InetAddress.getLoopbackAddress(), inputHandler.getCliOptions().advanced.host);
    }

    @Test
    void testCustomHost() throws IOException, ExitException {
        CliInputHandler inputHandler = this.runCli(args -> args.with(CliArgument.HOST, ANY_HOST)).inputHandler();
        assertEquals(InetAddress.getByName(ANY_HOST), inputHandler.getCliOptions().advanced.host);
    }

    @Test
    void testLocalhostHostname() throws IOException, ExitException {
        CliInputHandler inputHandler = this.runCli(args -> args.with(CliArgument.HOST, LOCALHOST)).inputHandler();
        assertEquals(InetAddress.getByName(LOCALHOST), inputHandler.getCliOptions().advanced.host);
    }

    @Test
    void testIPv6Loopback() throws IOException, ExitException {
        CliInputHandler inputHandler = this.runCli(args -> args.with(CliArgument.HOST, IPV6_LOOPBACK)).inputHandler();
        assertEquals(InetAddress.getByName(IPV6_LOOPBACK), inputHandler.getCliOptions().advanced.host);
    }

    @Test
    void testInvalidHost() {
        assertThrowsExactly(CliException.class, () -> {
            this.runCli(args -> args.with(CliArgument.HOST, INVALID_HOST_WITH_DOMAIN));
        });
    }

    @Test
    void testInvalidHostMessage() {
        CliException exception = assertThrowsExactly(CliException.class, () -> {
            this.runCli(args -> args.with(CliArgument.HOST, INVALID_HOST));
        });
        assertTrue(exception.getCause().getCause().getMessage().contains("Invalid bind address: " + INVALID_HOST));
    }

    @Override
    public void addDefaultParameters() {
        // prevents the submission directory from being added to the parameters automatically
    }
}
