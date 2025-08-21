package de.jplag.highlightextraction.evaluation;

import java.util.*;

import de.jplag.JPlagComparison;

/**
 * This class ist used by Weighting evaluation to Label the Datasets.
 */
public class LabelledWeighting {

    public enum DatasetType {
        A3,
        PROG19,
        PROG56
    }

    private final List<JPlagComparison> plagiatComparisons = new ArrayList<>();
    private final List<JPlagComparison> suspiciousComparisons = new ArrayList<>();
    private final List<JPlagComparison> unsuspiciousComparisons = new ArrayList<>();

    public void classifyComparisons(List<JPlagComparison> comparisons, DatasetType datasetType) {
        double plagiatThreshold;
        double auffaelligThreshold;

        switch (datasetType) {
            case PROG19 -> {
                plagiatThreshold = 0.74;
                auffaelligThreshold = 0.60;
            }
            case PROG56 -> {
                plagiatThreshold = 0.76;
                auffaelligThreshold = 0.60;
            }
            case A3 -> {
                plagiatThreshold = 0.71;
                auffaelligThreshold = 0.51;
            }
            default -> {
                throw new IllegalStateException("Dataset not found");
            }
        }

        for (JPlagComparison comparison : comparisons) {
            double sim = comparison.similarity();
            if (sim >= plagiatThreshold) {
                plagiatComparisons.add(comparison);
            } else if (sim >= auffaelligThreshold) {
                suspiciousComparisons.add(comparison);
            } else {
                unsuspiciousComparisons.add(comparison);
            }
        }
    }

    public List<JPlagComparison> getPlagiatComparisons() {
        return plagiatComparisons;
    }

    public List<JPlagComparison> getSuspiciousComparisons() {
        return suspiciousComparisons;
    }

    public List<JPlagComparison> getUnsuspiciousComparisons() {
        return unsuspiciousComparisons;
    }
}
