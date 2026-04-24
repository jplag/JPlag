package de.jplag.cli;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.jplag.cli.picocli.ParameterLabelRenderer;

import picocli.CommandLine;

/**
 * Tests for the custom {@link ParameterLabelRenderer}.
 */
class ParameterLabelRendererTest {
    private CommandLine.Help.IParamLabelRenderer paramLabelRenderer;
    private CommandLine.Help.IParamLabelRenderer baseLabelRenderer;

    private CommandLine.Help.Ansi ansi;
    private List<CommandLine.Help.Ansi.IStyle> styles;

    private static final String EXPECTED_ENUM_LABEL = "=<{FIRST, SECOND, THIRD}>";

    /**
     * Creates the parameterLabelRenderer, the base renderer and formatting information for picocli.
     */
    @BeforeEach
    void setup() {
        CommandLine commandLine = new CommandLine(CommandLine.Model.CommandSpec.create());
        CommandLine.Help help = commandLine.getHelp();

        this.baseLabelRenderer = help.parameterLabelRenderer();
        this.paramLabelRenderer = new ParameterLabelRenderer(this.baseLabelRenderer);

        this.ansi = help.ansi();
        this.styles = help.colorScheme().optionStyles();
    }

    /**
     * Tests if enums are rendered correctly.
     */
    @Test
    void testRenderEnum() {
        CommandLine.Model.ArgSpec argument = CommandLine.Model.OptionSpec.builder("enum").type(TestEnum.class).build();

        String label = this.paramLabelRenderer.renderParameterLabel(argument, this.ansi, this.styles).plainString();
        Assertions.assertEquals(EXPECTED_ENUM_LABEL, label);
    }

    /**
     * Tests, that a bunch of parameter types produces the same label as the default renderer from picocli.
     * @param parameterType The type for the option
     */
    @ParameterizedTest
    @ValueSource(classes = {Integer.class, String.class, Boolean.class})
    void testRenderDifferentTypes(Class<?> parameterType) {
        CommandLine.Model.ArgSpec argument = CommandLine.Model.OptionSpec.builder("test").type(parameterType).build();

        String baseLabel = this.baseLabelRenderer.renderParameterLabel(argument, this.ansi, this.styles).plainString();
        String customLabel = this.paramLabelRenderer.renderParameterLabel(argument, this.ansi, this.styles).plainString();
        Assertions.assertEquals(baseLabel, customLabel);
    }

    /**
     * Tests that both the custom and the base label renderer return the same separator.
     */
    @Test
    void testSameSeparator() {
        Assertions.assertEquals(this.baseLabelRenderer.separator(), this.paramLabelRenderer.separator());
    }

    /**
     * Enum used as for testing the label.
     */
    @SuppressWarnings("unused")
    enum TestEnum {
        FIRST,
        SECOND,
        THIRD
    }
}
