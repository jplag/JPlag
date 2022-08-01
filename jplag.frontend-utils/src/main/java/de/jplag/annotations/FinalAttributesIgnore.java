package de.jplag.annotations;

/**
 * Ignore 'Class variable fields should not have public accessibility'
 * @author Dominik Fuchss
 */
@SuppressWarnings("java:S2387")
public @interface FinalAttributesIgnore {
    String details();
}
