package de.jplag.scxml;

import de.jplag.TokenType;

/**
 * SCXML token type. Defines which tokens can be extracted from a statechart.
 */
public enum ScxmlTokenType implements TokenType {

    TRANSITION("Transition"),
    TRANSITION_END("Transition end", true),
    GUARDED_TRANSITION("Guarded transition"),
    TIMED_TRANSITION("Timed transition"),
    STATE("State begin"),
    STATE_END("State end", true),
    REGION("Region"),
    PARALLEL_STATE("Parallel state"),
    PARALLEL_REGION("Parallel region"),
    ON_ENTRY("OnEntry"),
    ON_EXIT("OnExit"),
    ACTION_END("Action end", true),
    // Executable content
    RAISE("Raise"),
    IF("If"),
    IF_END("If end", true),
    ELSE_IF("Else if"),
    ELSE_IF_END("Else if end", true),
    ELSE("Else"),
    ELSE_END("Else end", true),
    FOREACH("For each"),
    ASSIGNMENT("Assignment"),
    CANCEL("Cancel"),
    SCRIPT("Script"),
    SEND("Send");

    private final String description;
    private boolean isEndToken = false;

    ScxmlTokenType(String description) {
        this.description = description;
    }

    /**
     * Creates a statechart token type that may be an end token. An end token represents a token that is always added after
     * all child tokens for a nested token such as STATE.
     * @param isEndToken indicates that the token is an end token
     */
    ScxmlTokenType(String description, boolean isEndToken) {
        this(description);
        this.isEndToken = isEndToken;
    }

    /**
     * @return the description for this token type
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return whether this token is an end token
     */
    public boolean isEndToken() {
        return isEndToken;
    }

}
