package de.jplag.emf.normalization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import de.jplag.emf.MetamodelTokenType;

/**
 * Comparator for normalizing the order in a model tree by sorting the elements of containment references according to
 * their token type and then according to the distributions of token types in their subtrees.
 */
public class ContainmentOrderNormalizer implements Comparator<EObject> {

    private final List<EObject> modelElementsToSort;
    private final Map<MetamodelTokenType, List<EObject>> paths;

    /**
     * Creates the normalizing comparator.
     * @param modelElementsToSort are all model elements to sort with the comparator (required for normalization process).
     */
    public ContainmentOrderNormalizer(List<EObject> modelElementsToSort) {
        this.modelElementsToSort = modelElementsToSort;
        paths = new EnumMap<>(MetamodelTokenType.class);
    }

    @Override
    public int compare(EObject first, EObject second) {
        MetamodelTokenType firstType = TokenExtractionRules.element2Token(first);
        MetamodelTokenType secondType = TokenExtractionRules.element2Token(second);

        // 0. comparison if token types are absent for one or more elements.
        if (firstType == null && secondType == null) {
            return 0;
        } else if (firstType == null) {
            return -1;
        } else if (secondType == null) {
            return 1;
        }

        // 1. comparison by token type
        int comparisonByType = Integer.compare(firstType.ordinal(), secondType.ordinal());
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

    private List<EObject> calculatePath(MetamodelTokenType type) {
        List<EObject> elements = modelElementsToSort.stream().filter(it -> type.equals(TokenExtractionRules.element2Token(it))).toList();

        // Generate token type distributions for the subtrees of the elements to sort:
        Map<EObject, List<Double>> subtreeVectors = new HashMap<>();
        elements.forEach(it -> subtreeVectors.put(it, TokenVectorGenerator.generateOccurenceVector(it.eAllContents())));

        // Calculate distance matrix:
        double[][] distances = new double[elements.size()][elements.size()];
        for (int from = 0; from < distances.length; from++) {
            for (int to = 0; to < distances.length; to++) {
                distances[from][to] = euclideanDistance(subtreeVectors.get(elements.get(from)), subtreeVectors.get(elements.get(to)));
            }
        }

        // Start with element that has the most tokens in the subtree:
        var max = elements.stream().max((first, second) -> Integer.compare(countSubtreeTokens(first), countSubtreeTokens(second))).orElseThrow();
        return calculatePath(elements, max, distances);
    }

    private static double euclideanDistance(List<Double> first, List<Double> second) {
        if (first.size() != second.size()) {
            throw new IllegalArgumentException("Lists must have the same size");
        }
        double sum = 0;
        for (int i = 0; i < first.size(); i++) {
            double diff = first.get(i) - second.get(i);
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    private static int countSubtreeTokens(EObject modelElement) {
        int count = 0;
        Iterator<EObject> iterator = modelElement.eAllContents();
        while (iterator.hasNext()) {
            if (TokenExtractionRules.element2Token(iterator.next()) != null) {
                count++;
            }
        }
        return count;
    }
}
