package de.jplag.end_to_end_testing.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The object contains required options for the endToEndt tests which are important for the test suite. The options were
 * determined by discussion which can be found at https://github.com/jplag/JPlag/issues/590.
 */
public record Options(@JsonProperty("minimum_token_match") Integer minimumTokenMatch) {

    /**
     * Compares like inside values with the passed object. is necessary to find the correct results in the deserialized json
     * file.
     */
    @JsonIgnore
    @Override
    public boolean equals(Object options) {
        if (options instanceof Options optionsCaseted) {
            return minimumTokenMatch == optionsCaseted.minimumTokenMatch();
        } else {
            return false;
        }
    }

    /**
     * Creates the hashCode for the current Options object
     */
    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hash(this);
    }

    /**
     * Creates a String with the contained values of the object
     */
    @JsonIgnore
    @Override
    public String toString() {
        return "Options [minimumTokenMatch=" + minimumTokenMatch + "]";
    }
}
