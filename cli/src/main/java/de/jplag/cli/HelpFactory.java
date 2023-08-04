package de.jplag.cli;

import picocli.CommandLine;

public class HelpFactory implements CommandLine.IHelpFactory {
    @Override
    public CommandLine.Help create(CommandLine.Model.CommandSpec commandSpec, CommandLine.Help.ColorScheme colorScheme) {
        return new CustomHelp(commandSpec, colorScheme);
    }
}
