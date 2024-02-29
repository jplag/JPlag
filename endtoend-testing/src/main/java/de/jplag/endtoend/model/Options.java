package de.jplag.endtoend.model;

import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The object contains required options for the endToEnd tests which are important for the test suite.
 */
public record Options(@JsonProperty Integer[] minimumTokenMatches, @JsonProperty String baseCodeDirectory) {

    private static final int[] defaultTokenMatches = new int[] {3, 9};

    /**
     * Initializes a new options object with minimumTokenMatch and baseCodeDirectory both being null
     */
    public Options() {
        this(null, null);
    }

    /**
     * Builds the list of all token matches that should be checked. That means all values from minimumTokenMatches and the
     * default values (3 and 9)
     * @return The values
     */
    public int[] getMinimumTokenMatches() {
        Integer[] configuredValues = minimumTokenMatches;
        if (configuredValues == null) {
            configuredValues = new Integer[0];
        }

        int[] values = new int[configuredValues.length + defaultTokenMatches.length];
        System.arraycopy(defaultTokenMatches, 0, values, 0, defaultTokenMatches.length);
        for (int i = 0; i < configuredValues.length; i++) {
            values[defaultTokenMatches.length + i] = configuredValues[i];
        }

        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Options options = (Options) o;
        return Arrays.equals(minimumTokenMatches, options.minimumTokenMatches) && Objects.equals(baseCodeDirectory, options.baseCodeDirectory);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(baseCodeDirectory);
        result = 31 * result + Arrays.hashCode(minimumTokenMatches);
        return result;
    }

    @Override
    public String toString() {
        return "Options{" + "minimumTokenMatches=" + Arrays.toString(minimumTokenMatches) + ", baseCodeDirectory='" + baseCodeDirectory + '\'' + '}';
    }
}
