package de.jplag.annotations;

/**
 * Ignore 'Child class fields should not shadow parent class fields'
 * @author Dominik Fuchss
 */
@SuppressWarnings("java:S1104")
public @interface ShadowParentIgnore {
    String details();
}
