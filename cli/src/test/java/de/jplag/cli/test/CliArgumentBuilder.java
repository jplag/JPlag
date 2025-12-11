package de.jplag.cli.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assumptions;

/**
 * Utility for building CLI argument arrays for testing. Supports named and positional arguments, including invalid
 * input cases.
 */
public class CliArgumentBuilder {
    private static final String LONG_OPTION_PREFIX = "--";
    private static final String SHORT_OPTION_PREFIX = "-";

    private final Map<CliArgument<?>, Object> namedArgs;
    private final List<Pair<CliArgument<?>, Object>> positionalArgs;

    /**
     * Creates a new empty CliArgumentBuilder.
     */
    public CliArgumentBuilder() {
        this.namedArgs = new HashMap<>();
        this.positionalArgs = new ArrayList<>();
    }

    private CliArgumentBuilder(Map<CliArgument<?>, Object> namedArgs, List<Pair<CliArgument<?>, Object>> positionalArgs) {
        this.namedArgs = namedArgs;
        this.positionalArgs = positionalArgs;
    }

    /**
     * Adds a valid argument with its value to the builder.
     * @param <T> the type of the argument value
     * @param argument the CLI argument
     * @param value the value to assign
     * @return the updated builder
     */
    public <T> CliArgumentBuilder with(CliArgument<T> argument, T value) {
        if (argument.isPositional()) {
            positionalArgs.add(new Pair<>(argument, value));
        } else {
            this.namedArgs.put(argument, value);
        }

        return this;
    }

    /**
     * Adds an invalid (raw string) value for a given argument, for negative testing.
     * @param <T> the type of the argument value
     * @param argument the CLI argument
     * @param value the invalid value as string
     */
    public <T> void withInvalid(CliArgument<T> argument, String value) {
        if (argument.isPositional()) {
            positionalArgs.add(new Pair<>(argument, value));
        } else {
            this.namedArgs.put(argument, value);
        }
    }

    /**
     * Adds a boolean flag argument with value true.
     * @param argument the CLI argument
     * @return the updated builder
     */
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

    /**
     * Creates a deep copy of this builder instance.
     * @return a new CliArgumentBuilder with copied arguments
     */
    public CliArgumentBuilder copy() {
        Map<CliArgument<?>, Object> namedArgsCopy = new HashMap<>(this.namedArgs);
        List<Pair<CliArgument<?>, Object>> positionalArgsCopy = new ArrayList<>(this.positionalArgs);
        return new CliArgumentBuilder(namedArgsCopy, positionalArgsCopy);
    }
}
