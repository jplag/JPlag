package de.jplag.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.jplag.cli.picocli.CustomHelp;
import de.jplag.cli.picocli.HelpFactory;
import de.jplag.cli.picocli.ParameterLabelRenderer;

import picocli.CommandLine;

/**
 * Tests for the {@link CustomHelp} class.
 */
class CustomHelpTests {
    private CommandLine.Help help;

    /**
     * Creates the help object.
     */
    @BeforeEach
    void setup() {
        CommandLine.Model.CommandSpec commandSpec = CommandLine.Model.CommandSpec.create();
        this.help = new HelpFactory().create(commandSpec, new CommandLine(commandSpec).getColorScheme());
    }

    /**
     * Tests, that the custom help object returns the custom label renderer.
     */
    @Test
    void testReturnsCustomRenderer() {
        Assertions.assertInstanceOf(ParameterLabelRenderer.class, this.help.parameterLabelRenderer(),
                "The custom help object returned the wrong ParamLabelRenderer type.");
    }
}
