package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class BaseCodeOptionTest extends CommandLineInterfaceTest {

    private static final String NAME = "BaseCodeName";

    @Test
    void testDefaultValue() throws CliException {
        buildOptionsFromCLI(defaultArguments());
        assertNull(options.baseCodeSubmissionDirectory());
    }

    @Test
    void testCustomName() throws CliException {
        buildOptionsFromCLI(defaultArguments().baseCode(NAME));
        assertEquals(NAME, options.baseCodeSubmissionDirectory().getName());
    }
}
