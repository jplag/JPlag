package de.jplag.endtoend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The object contains required options for the endToEndt tests which are important for the test suite. The options were
 * determined by discussion which can be found at https://github.com/jplag/JPlag/issues/590.
 */
public record Options(@JsonProperty("minimum_token_match") Integer minimumTokenMatch) {
}
