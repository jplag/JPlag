package de.jplag.emf.normalization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import de.jplag.emf.MetamodelTokenType;

/**
 * Utility class for the generation of token occurrence histograms for model subtrees.
 */
public final class TokenVectorGenerator {

    private TokenVectorGenerator() {
        // private constructor for non-instantiability.
    }

    /**
     * Generate a token occurrence vector for a subtree of a model.
     * @param modelElements is a visitor for the subtree.
     * @return a list, where each entry represents the number of tokens in the subtree. The order is determined by
     * {@link MetamodelTokenType}.
     */
    public static List<Double> generateOccurenceVector(Iterator<EObject> modelElements) {
        Map<MetamodelTokenType, Integer> tokenTypeHistogram = new EnumMap<>(MetamodelTokenType.class);

        while (modelElements.hasNext()) {
            EObject eObject = modelElements.next();
            MetamodelTokenType tokenType = TokenExtractionRules.element2Token(eObject);
            if (tokenType != null) {
                tokenTypeHistogram.merge(tokenType, 1, Integer::sum);
            }
        }
        List<Integer> occurenceVector = new ArrayList<>();
        for (MetamodelTokenType type : MetamodelTokenType.values()) {
            occurenceVector.add(tokenTypeHistogram.getOrDefault(type, 0));
        }
        return normalize(occurenceVector);
    }

    public static List<Double> normalize(List<Integer> vector) {
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
