package de.jplag.end_to_end_testing.modelRecord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
     * Creates the hashCode for the AKtuelle Options object
     */
    @JsonIgnore
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + minimumTokenMatch;
        return result;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "Options [minimumTokenMatch=" + minimumTokenMatch + "]";
    }
}
