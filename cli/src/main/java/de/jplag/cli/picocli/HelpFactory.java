package de.jplag.cli.picocli;

import picocli.CommandLine;

/**
 * Custom help factory, used to add the custom {@link ParamLabelRenderer}.
 */
public class HelpFactory implements CommandLine.IHelpFactory {
    @Override
    public CommandLine.Help create(CommandLine.Model.CommandSpec commandSpec, CommandLine.Help.ColorScheme colorScheme) {
        return new CustomHelp(commandSpec, colorScheme);
    }
}
