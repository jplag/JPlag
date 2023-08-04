package de.jplag.cli;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import picocli.CommandLine;

public class ParamLabelRenderer implements CommandLine.Help.IParamLabelRenderer {
    private final CommandLine.Help.IParamLabelRenderer base;

    public ParamLabelRenderer(CommandLine.Help.IParamLabelRenderer base) {
        this.base = base;
    }

    @Override
    public CommandLine.Help.Ansi.Text renderParameterLabel(CommandLine.Model.ArgSpec argSpec, CommandLine.Help.Ansi ansi,
            List<CommandLine.Help.Ansi.IStyle> styles) {
        if (argSpec.type().isEnum()) {
            @SuppressWarnings("unchecked")
            Enum<?>[] enumConstants = ((Class<Enum<?>>) argSpec.type()).getEnumConstants();
            String enumValueNames = Arrays.stream(enumConstants).map(Enum::name).collect(Collectors.joining(","));
            return CommandLine.Help.Ansi.AUTO.text(String.format("=<{%s}>", enumValueNames));
        }

        return base.renderParameterLabel(argSpec, ansi, styles);
    }

    @Override
    public String separator() {
        return base.separator();
    }
}
