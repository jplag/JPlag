package de.jplag.cli;

import picocli.CommandLine;

public class CustomHelp extends CommandLine.Help {
    private final IParamLabelRenderer paramLabelRenderer;

    public CustomHelp(CommandLine.Model.CommandSpec command, ColorScheme colorScheme) {
        super(command, colorScheme);

        this.paramLabelRenderer = new ParamLabelRenderer(super.parameterLabelRenderer());
    }

    @Override
    public IParamLabelRenderer parameterLabelRenderer() {
        return this.paramLabelRenderer;
    }
}
