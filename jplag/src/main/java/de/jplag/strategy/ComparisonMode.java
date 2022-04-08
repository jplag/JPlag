package de.jplag.strategy;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * The comparison mode determines which {@link ComparisonStrategy} is used to compare submissions.
 */
public enum ComparisonMode {
    NORMAL("Normal comparison (sequential)"),
    PARALLEL("Faster comparison (parallel)");

    private final String name;
    private final String description;

    private ComparisonMode(String description) {
        this.description = description;
        name = toString().toLowerCase();
    }

    /**
     * @return the specifier for the comparison mode.
     */
    public String getName() {
        return name;
    }

    /**
     * The textual description of the comparison mode.
     * @return a description of the comparison mode
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return a collections of all mode names.
     */
    public static Collection<String> allNames() {
        return Arrays.stream(values()).map(ComparisonMode::getName).collect(toList());
    }

    /**
     * Retrieves a comparison mode from a specific name.
     * @param name is that name.
     * @return the optional mode or nothing if no name matches.
     */
    public static Optional<ComparisonMode> fromName(String name) {
        return Arrays.stream(values()).filter(it -> it.name.equals(name)).findFirst();
    }
}