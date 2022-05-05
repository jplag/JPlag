package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.jplag.CommandLineArgument;

class BaseCodeOptionTest extends CommandLineInterfaceTest {

    private static final String NAME = "BaseCodeName";

    @Test
    void testDefaultValue() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(Optional.empty(), options.getBaseCodeSubmissionName());
    }

    @Test
    void testCustomName() {
        String argument = buildArgument(CommandLineArgument.BASE_CODE, NAME);
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(NAME, options.getBaseCodeSubmissionName().get());
    }
}
