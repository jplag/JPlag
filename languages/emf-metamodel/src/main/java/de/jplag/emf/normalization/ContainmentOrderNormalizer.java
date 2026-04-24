package de.jplag.emf.normalization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import de.jplag.TokenType;
import de.jplag.emf.parser.ModelingElementTokenizer;

/**
 * Comparator for normalizing the order in a model tree by sorting the elements of containment references according to
 * their token type and then according to the distributions of token types in their subtrees.
 */
public class ContainmentOrderNormalizer implements Comparator<EObject> {

    private final List<EObject> modelElementsToSort;
    private final Map<TokenType, List<EObject>> paths;
    private final ModelingElementTokenizer tokenizer;
    private final TokenVectorGenerator tokenVectorGenerator;

    /**
     * Creates the normalizing comparator.
     * @param modelElementsToSort are all model elements to sort with the comparator (required for normalization process).
     * @param tokenizer is the tokenizer that assigns tokens to model elements.
     */
    public ContainmentOrderNormalizer(List<EObject> modelElementsToSort, ModelingElementTokenizer tokenizer) {
        this.modelElementsToSort = modelElementsToSort;
        this.tokenizer = tokenizer;
        paths = new HashMap<>();
        tokenVectorGenerator = new TokenVectorGenerator(tokenizer);
    }

    @Override
    public int compare(EObject first, EObject second) {
        TokenType firstType = tokenizer.element2Token(first);
        TokenType secondType = tokenizer.element2Token(second);

        // 0. comparison if token types are absent for one or more elements.
        if (firstType == null && secondType == null) {
            return 0;
        }
        if (firstType == null) {
            return -1;
        }
        if (secondType == null) {
            return 1;
        }

        // 1. comparison by token type
        int comparisonByType = firstType.toString().compareTo(secondType.toString());
        if (comparisonByType != 0) {
            return comparisonByType;
        }

        // 2. compare by position of the nearest neighbor path of the token distribution vectors of the elements subtrees.
        List<EObject> path = paths.computeIfAbsent(firstType, this::calculatePath);
        return path.indexOf(first) - path.indexOf(second);
    }

    private List<EObject> calculatePath(List<EObject> elements, EObject start, double[][] distances) {
        List<EObject> path = new ArrayList<>();
        Set<EObject> remaining = new HashSet<>(elements);
        EObject current = start;
        remaining.remove(current);
        path.add(current);
        while (!remaining.isEmpty()) {
            double shortestDistance = Double.MAX_VALUE;
            EObject next = null;
            for (EObject potentialNext : remaining) {
                double distance = distances[elements.indexOf(current)][elements.indexOf(potentialNext)];
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    next = potentialNext;
                } else if (distance == shortestDistance && modelElementsToSort.indexOf(potentialNext) < modelElementsToSort.indexOf(next)) {
                    next = potentialNext; // Sort according to original order if equal
                }
            }
            current = next;
            remaining.remove(current);
            path.add(current);
        }
        return path;
    }

    private List<EObject> calculatePath(TokenType type) {
        List<EObject> elements = modelElementsToSort.stream().filter(it -> type.equals(tokenizer.element2Token(it))).toList();

        // Generate token type distributions for the subtrees of the elements to sort:
        Map<EObject, TokenOccurenceVector> subtreeVectors = new HashMap<>();
        elements.forEach(it -> subtreeVectors.put(it, tokenVectorGenerator.generateOccurenceVector(it.eAllContents())));

        // Calculate distance matrix:
        double[][] distances = new double[elements.size()][elements.size()];
        for (int from = 0; from < distances.length; from++) {
            for (int to = 0; to < distances.length; to++) {
                distances[from][to] = euclideanDistance(subtreeVectors.get(elements.get(from)), subtreeVectors.get(elements.get(to)));
            }
        }

        // Start with element that has the most tokens in the subtree:
        var max = Collections.max(elements, (first, second) -> Integer.compare(countSubtreeTokens(first), countSubtreeTokens(second)));
        return calculatePath(elements, max, distances);
    }

    private int countSubtreeTokens(EObject modelElement) {
        int count = 0;
        Iterator<EObject> iterator = modelElement.eAllContents();
        while (iterator.hasNext()) {
            if (tokenizer.element2Token(iterator.next()) != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the euclidean distance for two token occurrence vectors. As they are zero-padded, they are virtually of
     * the same length.
     */
    private static double euclideanDistance(TokenOccurenceVector first, TokenOccurenceVector second) {
        double sum = 0;
        for (int i = 0; i < first.size(); i++) {
            double diff = first.get(i) - second.get(i);
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

}
