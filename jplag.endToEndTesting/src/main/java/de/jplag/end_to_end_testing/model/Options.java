package de.jplag.end_to_end_testing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Options {
    @JsonProperty("minimum_token_match")
    private int minimumTokenMatch;

    public Options(int minimumTokenMatch) {
        this.minimumTokenMatch = minimumTokenMatch;
    }

    /**
     * empty constructor in case the serialization contains an empty object to prevent throwing exceptions. this constructor
     * was necessary for serialization with the Jackson parse extension
     */
    public Options() {
        // For Serialization
    }

    @JsonIgnore
    public int getMinimumTokenMatch() {
        return minimumTokenMatch;
    }

    @JsonIgnore
    @Override
    public boolean equals(Object options) {
        if (options instanceof Options) {
            return minimumTokenMatch == ((Options) options).getMinimumTokenMatch();
        } else {
            return false;
        }

    }

    @JsonIgnore
    @Override
    public String toString() {
        return "Options [minimumTokenMatch=" + minimumTokenMatch + "]";
    }
}
