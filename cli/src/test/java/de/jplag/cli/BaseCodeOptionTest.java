package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliResult;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class BaseCodeOptionTest extends CliTest {

    private static final String NAME = "BaseCodeName";

    @Test
    void testDefaultValue() throws ExitException, IOException {
        JPlagOptions options = runCli().jPlagOptions();

        assertNull(options.baseCodeSubmissionDirectory());
    }

    @Test
    void testCustomName() throws ExitException, IOException {
        CliResult result = runCli(options -> options.with(CliArgument.BASE_CODE, NAME));
        JPlagOptions options = result.jPlagOptions();

        assertEquals(NAME, options.baseCodeSubmissionDirectory().getName());
    }
}
