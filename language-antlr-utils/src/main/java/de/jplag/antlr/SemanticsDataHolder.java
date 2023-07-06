package de.jplag.antlr;

import de.jplag.semantics.VariableRegistry;

/**
 * Holds all required context objects for the generation of code semantics
 * @param registry The variable registry
 */
public record SemanticsDataHolder(VariableRegistry registry) {
}