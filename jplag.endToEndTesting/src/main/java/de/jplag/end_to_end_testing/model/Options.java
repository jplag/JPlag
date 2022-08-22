package de.jplag.end_to_end_testing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class manages the test options and thus allows testing of different JPlag options for the test cases.
 */
public class Options {
    @JsonProperty("minimum_token_match")
    private int minimumTokenMatch;

    /**
     * Constructor for the Options. The model is the serialization of the Json file in the form of a Java object.
     * @param minimumTokenMatch for which the test options are to be created
     */
    public Options(int minimumTokenMatch) {
        this.minimumTokenMatch = minimumTokenMatch;
    }

    /**
     * Empty constructor in case the serialization contains an empty object to prevent throwing exceptions. this constructor
     * was necessary for serialization with the Jackson parse extension
     */
    public Options() {
        // For Serialization
    }

    /**
     * @return to testendet minimum token match value
     */
    @JsonIgnore
    public int getMinimumTokenMatch() {
        return minimumTokenMatch;
    }

    /**
     * Compares like inside values with the passed object. is necessary to find the correct results in the deserialized json
     * file.
     */
    @JsonIgnore
    @Override
    public boolean equals(Object options) {
        if (options instanceof Options) {
            return minimumTokenMatch == ((Options) options).getMinimumTokenMatch();
        } else {
            return false;
        }

    }

    /**
     * Provides the necessary string for the eventual specification in the testcases to make them better traceable.
     */
    @JsonIgnore
    @Override
    public String toString() {
        return "Options [minimumTokenMatch=" + minimumTokenMatch + "]";
    }
}
