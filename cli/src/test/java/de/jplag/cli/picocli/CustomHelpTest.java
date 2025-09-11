package de.jplag.cli.picocli;

import de.jplag.cli.options.CliOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

public class CustomHelpTest {
    @Test
    void testPrintOptions_rootSpec() {
        CommandLine.Model.CommandSpec spec = CommandLine.Model.CommandSpec.forAnnotatedObject(new CliOptions());
        CustomHelp help = new CustomHelp(spec, new CommandLine.Help.ColorScheme.Builder().build());
        Assertions.assertNotEquals("", help.optionList());
    }

    @Test
    void testPrintOptions_subcommandSpecValidOptions() {
        CommandLine.Model.CommandSpec spec = CommandLine.Model.CommandSpec.create();
        spec.addOption(CommandLine.Model.OptionSpec.builder("test").build());
        CustomHelp help = new CustomHelp(spec, new CommandLine.Help.ColorScheme.Builder().build());
        Assertions.assertNotEquals("", help.optionList());
    }

    @Test
    void testPrintOptions_subcommandSpecInvalidOptions() {
        CommandLine.Model.CommandSpec spec = CommandLine.Model.CommandSpec.create();
        spec.addMixin("test", CommandLine.Model.CommandSpec.forAnnotatedObject(new CliOptions()));
        CustomHelp help = new CustomHelp(spec, new CommandLine.Help.ColorScheme.Builder().build());
        Assertions.assertEquals("", help.optionList());
    }

    @Test
    void testPrintPositions_rootSpec() {
        CommandLine.Model.CommandSpec spec = CommandLine.Model.CommandSpec.forAnnotatedObject(new CliOptions());
        CustomHelp help = new CustomHelp(spec, new CommandLine.Help.ColorScheme.Builder().build());
        Assertions.assertNotEquals("", help.parameterList());
    }

    @Test
    void testPrintPositions_subcommandSpec() {
        CommandLine.Model.CommandSpec spec = CommandLine.Model.CommandSpec.create();
        spec.addMixin("test", CommandLine.Model.CommandSpec.forAnnotatedObject(new CliOptions()));
        CustomHelp help = new CustomHelp(spec, new CommandLine.Help.ColorScheme.Builder().build());
        Assertions.assertEquals("", help.parameterList());
    }
}
