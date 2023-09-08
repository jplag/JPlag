package de.jplag.scxml;

import de.jplag.TokenType;

/**
 * SCXML token type. Defines which tokens can be extracted from a statechart.
 */
public enum ScxmlTokenType implements TokenType {

    /**
     * Token for a transition.
     */
    TRANSITION("Transition"),

    /**
     * Token extracted after visiting all child elements of a transition.
     */
    TRANSITION_END("Transition end", true),

    /**
     * Token for a guarded transition.
     */
    GUARDED_TRANSITION("Guarded transition"),

    /**
     * Token for a timed transition.
     */
    TIMED_TRANSITION("Timed transition"),

    /**
     * Token for a state.
     */
    STATE("State"),

    /**
     * Token for an initial state.
     */
    INITIAL_STATE("Initial state"),

    /**
     * Token extracted after visiting all child elements of a state.
     */
    STATE_END("State end", true),

    /**
     * Token for a region.
     */
    REGION("Region"),

    /**
     * Token for an initial region.
     */
    INITIAL_REGION("Initial region"),

    /**
     * Token for an OnEntry action.
     */
    ON_ENTRY("OnEntry"),

    /**
     * Token for an OnExit action.
     */
    ON_EXIT("OnExit"),

    /**
     * Token extracted after visiting all executable contents of an action (OnEntry or OnExit).
     */
    ACTION_END("Action end", true),

    /**
     * Token for the executable content raise.
     */
    RAISE("Raise"),

    /**
     * Token for the executable content if.
     */
    IF("If"),

    /**
     * Token extracted at the end of the executable content if.
     */
    IF_END("If end", true),

    /**
     * Token for the executable content raise.
     */
    ELSE_IF("Else if"),

    /**
     * Token for the end of a branch (else if or else).
     */
    ELSE_IF_END("Branch end", true),

    /**
     * Token for an else action.
     */
    ELSE("Else"),

    /**
     * Token for the end of an else action.
     */
    ELSE_END("Else end", true),

    /**
     * Token for the executable content foreach.
     */
    FOREACH("For each"),

    /**
     * Token for the executable content assignment.
     */
    ASSIGNMENT("Assignment"),

    /**
     * Token for the executable content foreach.
     */
    CANCEL("Cancel"),

    /**
     * Token for the executable content script.
     */
    SCRIPT("Script"),

    /**
     * Token for the executable content send.
     */
    SEND("Send");

    private final String description;
    private boolean isEndToken = false;

    /**
     * Constructs a new SCXML token type with a description.
     * @param description the description for this token type
     */
    ScxmlTokenType(String description) {
        this.description = description;
    }

    /**
     * Creates a statechart token type that may be an end token. An end token represents a token that is always added after
     * all child tokens for a nested token such as STATE.
     * @param description the description for this token type
     * @param isEndToken indicates that the token is an end token
     */
    ScxmlTokenType(String description, boolean isEndToken) {
        this(description);
        this.isEndToken = isEndToken;
    }

    /**
     * @return the description for this token type
     */
    @Override
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
