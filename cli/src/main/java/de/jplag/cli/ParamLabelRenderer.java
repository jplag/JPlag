package de.jplag.cli;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import picocli.CommandLine;

/**
 * Custom implementation of {@link picocli.CommandLine.Help.IParamLabelRenderer}, that show the available options for
 * enums. For all other parameter types, the base renderer is called.
 */
public class ParamLabelRenderer implements CommandLine.Help.IParamLabelRenderer {
    private final CommandLine.Help.IParamLabelRenderer base;

    private static final String PARAM_LABEL_PATTERN = "=<{%s}>";
    private static final String VALUE_SEPARATOR = ", ";

    /**
     * New instance
     * @param base The base renderer used for all non enum types
     */
    public ParamLabelRenderer(CommandLine.Help.IParamLabelRenderer base) {
        this.base = base;
    }

    @Override
    public CommandLine.Help.Ansi.Text renderParameterLabel(CommandLine.Model.ArgSpec argSpec, CommandLine.Help.Ansi ansi,
            List<CommandLine.Help.Ansi.IStyle> styles) {
        if (argSpec.type().isEnum()) {
            @SuppressWarnings("unchecked")
            Enum<?>[] enumConstants = ((Class<Enum<?>>) argSpec.type()).getEnumConstants();
            String enumValueNames = Arrays.stream(enumConstants).map(Enum::name).collect(Collectors.joining(VALUE_SEPARATOR));
            return CommandLine.Help.Ansi.AUTO.text(String.format(PARAM_LABEL_PATTERN, enumValueNames));
        }

        return base.renderParameterLabel(argSpec, ansi, styles);
    }

    @Override
    public String separator() {
        return base.separator();
    }
}
