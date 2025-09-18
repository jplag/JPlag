package de.jplag.cli.picocli;

import picocli.CommandLine;

/**
 * Custom implementation for the help page, including the custom {@link ParameterLabelRenderer}.
 */
public class CustomHelp extends CommandLine.Help {
    private final IParamLabelRenderer paramLabelRenderer;

    /**
     * Created a custom help text.
     * @param command The {@link picocli.CommandLine.Model.CommandSpec} to build the help for
     * @param colorScheme The {@link picocli.CommandLine.Help.ColorScheme} for the help page
     */
    public CustomHelp(CommandLine.Model.CommandSpec command, ColorScheme colorScheme) {
        super(command, colorScheme);

        this.paramLabelRenderer = new ParameterLabelRenderer(super.parameterLabelRenderer());
    }

    @Override
    public IParamLabelRenderer parameterLabelRenderer() {
        return this.paramLabelRenderer;
    }
}
