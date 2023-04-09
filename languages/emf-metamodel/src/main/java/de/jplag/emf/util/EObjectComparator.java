package de.jplag.emf.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.emf.MetamodelTokenType;
import de.jplag.emf.parser.TokenExtractionRules;

public class EObjectComparator implements Comparator<EObject> {

    private EClassVectorGenerator generator;
    private List<EObject> modelElementsToSort;
    private Map<MetamodelTokenType, List<EObject>> paths;

    private Logger logger;

    public EObjectComparator(EClassVectorGenerator generator, List<EObject> modelElementsToSort) {
        this.generator = generator;
        this.modelElementsToSort = modelElementsToSort;
        paths = new HashMap<>();
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    private List<EObject> calculatePath(MetamodelTokenType type) {
        List<EObject> elements = modelElementsToSort.stream()
                .filter(it -> TokenExtractionRules.element2Token(it) != null && TokenExtractionRules.element2Token(it).equals(type)).toList();
        Map<EObject, List<Double>> subtreeVectors = new HashMap<>();
        elements.forEach(it -> subtreeVectors.put(it, generator.generateEClassHistogram(it.eAllContents())));
        double[][] distances = new double[elements.size()][elements.size()];
        for (int from = 0; from < distances.length; from++) {
            for (int to = 0; to < distances.length; to++) {
                distances[from][to] = euclideanDistance(subtreeVectors.get(elements.get(from)), subtreeVectors.get(elements.get(to)));
            }
        }
        logger.error("Distances for " + type);
        logger.error("    elements: " + elements.size());
        logger.error("    vectors: ");
        elements.forEach(it -> logger.error("      " + iteratorSize(it.eAllContents()) + " in " + subtreeVectors.get(it)));
        for (double[] row : distances) {
            logger.error(Arrays.toString(row));
        }
//        Map<EObject, Double> euclideanDistancesToOrigin = new HashMap<>();
//        for (EObject element : elements) {
//            double sum = subtreeHistograms.get(element).stream().mapToDouble(Double::doubleValue).sum();
//            euclideanDistancesToOrigin.put(element, sum);
//        }
 //       logger.error("Euclideans to origin: " + euclideanDistancesToOrigin.values());
  //      var emax = euclideanDistancesToOrigin.entrySet().stream().max((first, second) -> Double.compare(first.getValue(), second.getValue()))
  //              .orElseThrow().getKey();
  //      logger.error("max: " + euclideanDistancesToOrigin.get(emax));
        var max = elements.stream().max((first, second) -> Integer.compare(iteratorSize(first.eAllContents()), iteratorSize(second.eAllContents())))
                .orElseThrow();
        /**
         * LESSON: Max is better than min and euclidean max
         */

        return calculatePath(elements, max, distances);
    }

    public static int iteratorSize(Iterator<EObject> iterator) {
        int count = 0;
        while (iterator.hasNext()) {
            if (TokenExtractionRules.element2Token(iterator.next()) != null) {
                count++;
            }
        }
        return count;
    }

    private List<EObject> calculatePath(List<EObject> elements, EObject start, double[][] distances) {
        List<EObject> path = new ArrayList<>();
        List<Double> mins = new ArrayList<>();
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
                } else if (distance == shortestDistance) {
                    if (modelElementsToSort.indexOf(potentialNext) < modelElementsToSort.indexOf(next)) {
                        next = potentialNext;
                    }
                }
            }
            if (next == null) {
                throw new IllegalStateException("Unable to find next element");
            }
            current = next;
            remaining.remove(current);
            path.add(current);
            mins.add(shortestDistance);
        }
        logger.error("Path: " + path);
        logger.error("step: " + mins);
        return path;
    }

    public static double euclideanDistance(List<Double> first, List<Double> second) {
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

    @Override
    public int compare(EObject first, EObject second) {

        MetamodelTokenType firstType = TokenExtractionRules.element2Token(first);
        MetamodelTokenType secondType = TokenExtractionRules.element2Token(second);
        if (firstType == null && secondType == null) {
            return 0;
        } else if (firstType == null) {
            return -1;
        } else if (secondType == null) {
            return 1;
        }

        int comparisonByType = Integer.compare(firstType.ordinal(), secondType.ordinal());
        if (comparisonByType != 0) {
            return comparisonByType;
        }

        List<EObject> path = paths.computeIfAbsent(firstType, it -> calculatePath(it));

        // List<MetamodelTokenType> fst = new ArrayList<>();
        // first.eAllContents().forEachRemaining(it -> fst.add(TokenExtractionRules.element2Token(it)));
        // List<MetamodelTokenType> scd = new ArrayList<>();
        // second.eAllContents().forEachRemaining(it -> scd.add(TokenExtractionRules.element2Token(it)));
        // return compareLexicographically(fst.stream().filter(it->it!=null).map(it->it.ordinal()).toList(),
        // scd.stream().filter(it->it!=null).map(it->it.ordinal()).toList());
        // return compareLexicographically(subtreeHistograms.get(first), subtreeHistograms.get(second));
        return path.indexOf(first) - path.indexOf(second);
    }

    private static int compareLexicographically(List<Integer> vector1, List<Integer> vector2) {
        int minLength = Math.min(vector1.size(), vector2.size());
        for (int i = 0; i < minLength; i++) {
            int comparison = Integer.compare(vector1.get(i), vector2.get(i));
            if (comparison != 0) {
                return comparison;
            }
        }
        return Integer.compare(vector1.size(), vector2.size());
    }

}
