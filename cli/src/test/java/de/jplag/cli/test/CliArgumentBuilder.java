package de.jplag.cli.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assumptions;

public class CliArgumentBuilder {
    private static final String LONG_OPTION_PREFIX = "--";
    private static final String SHORT_OPTION_PREFIX = "-";

    private final Map<CliArgument<?>, Object> namedArgs;
    private final List<Pair<CliArgument<?>, Object>> positionalArgs;

    public CliArgumentBuilder() {
        this.namedArgs = new HashMap<>();
        this.positionalArgs = new ArrayList<>();
    }

    private CliArgumentBuilder(Map<CliArgument<?>, Object> namedArgs, List<Pair<CliArgument<?>, Object>> positionalArgs) {
        this.namedArgs = namedArgs;
        this.positionalArgs = positionalArgs;
    }

    public <T> CliArgumentBuilder with(CliArgument<T> argument, T value) {
        if (argument.isPositional()) {
            positionalArgs.add(new Pair<>(argument, value));
        } else {
            this.namedArgs.put(argument, value);
        }

        return this;
    }

    public <T> void withInvalid(CliArgument<T> argument, String value) {
        if (argument.isPositional()) {
            positionalArgs.add(new Pair<>(argument, value));
        } else {
            this.namedArgs.put(argument, value);
        }
    }

    public CliArgumentBuilder with(CliArgument<Boolean> argument) {
        with(argument, true);
        return this;
    }

    String[] buildArguments() {
        List<String> values = new ArrayList<>();

        this.namedArgs.forEach((arg, value) -> {
            values.addAll(List.of(formatArgNameAndValue(arg.name(), value)));
        });

        this.positionalArgs.forEach(arg -> {
            values.add(formatArgValue(arg.getValue()));
        });

        return values.toArray(new String[0]);
    }

    private String[] formatArgNameAndValue(String name, Object value) {
        String valueText = formatArgValue(value);
        if (name.length() == 1) {
            if (valueText.isEmpty()) {
                return new String[] {SHORT_OPTION_PREFIX + name};
            } else {
                return new String[] {SHORT_OPTION_PREFIX + name, valueText};
            }
        }
        if (valueText.isEmpty()) {
            return new String[] {LONG_OPTION_PREFIX + name};
        } else {
            return new String[] {LONG_OPTION_PREFIX + name + "=" + formatArgValue(value)};
        }
    }

    private String formatArgValue(Object value) {
        return switch (value) {
            case String[] array -> String.join(",", array);
            case String string -> string;
            case Number number -> number.toString();
            case Boolean ignored -> "";
            default -> Assumptions.abort("Missing formatter for given type.");
        };
    }

    public CliArgumentBuilder copy() {
        Map<CliArgument<?>, Object> namedArgsCopy = new HashMap<>(this.namedArgs);
        List<Pair<CliArgument<?>, Object>> positionalArgsCopy = new ArrayList<>(this.positionalArgs);
        return new CliArgumentBuilder(namedArgsCopy, positionalArgsCopy);
    }
}
