package de.jplag.comparison;

import java.util.List;

import de.jplag.Submission;
import de.jplag.Token;

@FunctionalInterface
public interface TokenListSupplier {
    List<Token> getTokenList(Submission submission);
}
