package de.jplag.emf.normalization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenType;
import de.jplag.emf.parser.ModelingElementTokenizer;

/**
 * Utility class for the generation of token occurrence histograms for model subtrees.
 */
public class TokenVectorGenerator {

    private final ModelingElementTokenizer tokenizer;

    /**
     * Creates the generator.
     * @param tokenizer is the tokenizer that assigns tokens to model elements.
     */
    public TokenVectorGenerator(ModelingElementTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * Generate a token occurrence vector for a subtree of a model.
     * @param modelElements is a visitor for the subtree.
     * @return a zero padded token occurrence vector, where each entry represents the number of tokens in the subtree. The
     * order is determined by {@link ModelingElementTokenizer#allTokenTypes()}.
     */
    public TokenOccurenceVector generateOccurenceVector(Iterator<EObject> modelElements) {
        Map<TokenType, Integer> tokenTypeHistogram = new HashMap<>();

        while (modelElements.hasNext()) {
            tokenizer.element2OptionalToken(modelElements.next()).ifPresent(it -> tokenTypeHistogram.merge(it, 1, Integer::sum));
        }
        List<Integer> occurenceVector = new ArrayList<>();
        for (TokenType type : tokenizer.allTokenTypes()) {
            occurenceVector.add(tokenTypeHistogram.getOrDefault(type, 0));
        }
        return new TokenOccurenceVector(normalize(occurenceVector));
    }

    private static List<Double> normalize(List<Integer> vector) {
        double magnitude = Math.sqrt(vector.stream().mapToInt(it -> it * it).sum());
        if (magnitude == 0) {
            return Collections.nCopies(vector.size(), 0.0);
        }
        List<Double> normalizedVector = new ArrayList<>();
        for (int element : vector) {
            double normalizedValue = element / magnitude;
            normalizedVector.add(normalizedValue);
        }
        return normalizedVector;
    }
}
