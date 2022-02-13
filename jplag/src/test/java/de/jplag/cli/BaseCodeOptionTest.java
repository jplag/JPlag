package de.jplag.cli;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import de.jplag.CommandLineArgument;

public class BaseCodeOptionTest extends CommandLineInterfaceTest {

    private static final String NAME = "BaseCodeName";

    @Test
    public void testDefaultValue() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(Optional.empty(), options.getBaseCodeSubmissionName());
    }

    @Test
    public void testCustomName() {
        String argument = buildArgument(CommandLineArgument.BASE_CODE, NAME);
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(NAME, options.getBaseCodeSubmissionName().get());
    }
}
