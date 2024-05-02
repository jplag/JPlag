package de.jplag.cli.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assumptions;

public class CliArgBuilder {
    private final Map<CliArg<?>, Object> namedArgs;
    private final List<Pair<CliArg<?>, Object>> positionalArgs;

    public CliArgBuilder() {
        this.namedArgs = new HashMap<>();
        this.positionalArgs = new ArrayList<>();
    }

    private CliArgBuilder(Map<CliArg<?>, Object> namedArgs, List<Pair<CliArg<?>, Object>> positionalArgs) {
        this.namedArgs = namedArgs;
        this.positionalArgs = positionalArgs;
    }

    public <T> CliArgBuilder with(CliArg<T> argument, T value) {
        if (argument.isPositional()) {
            positionalArgs.add(new Pair<>(argument, value));
        } else {
            this.namedArgs.put(argument, value);
        }

        return this;
    }

    public <T> void withInvalid(CliArg<T> argument, String value) {
        if (argument.isPositional()) {
            positionalArgs.add(new Pair<>(argument, value));
        } else {
            this.namedArgs.put(argument, value);
        }
    }

    public void with(CliArg<Boolean> argument) {
        with(argument, true);
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
                return new String[] {"-" + name};
            } else {
                return new String[] {"-" + name, valueText};
            }
        } else {
            if (valueText.isEmpty()) {
                return new String[] {"--" + name};
            } else {
                return new String[] {"--" + name + "=" + formatArgValue(value)};
            }
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

    public CliArgBuilder copy() {
        Map<CliArg<?>, Object> namedArgsCopy = new HashMap<>(this.namedArgs);
        List<Pair<CliArg<?>, Object>> positionalArgsCopy = new ArrayList<>(this.positionalArgs);
        return new CliArgBuilder(namedArgsCopy, positionalArgsCopy);
    }
}
