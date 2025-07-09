package de.jplag.highlight_extraction.frequencyDetermination;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.highlight_extraction.FrequencySimilarity;

import java.util.*;

/**
 * Diese Klasse übernimmt die Gruppierung von JPlagComparisons
 * in die Kategorien: Plagiat, Auffällig und Unauffällig,
 * unabhängig von den Submissions.
 */
public class LabelledWeighting {

    public enum DatasetType {
        A3, DS19, DS56
    }

    private final List<JPlagComparison> plagiatComparisons = new ArrayList<>();
    private final List<JPlagComparison> auffaelligComparisons = new ArrayList<>();
    private final List<JPlagComparison> unauffaelligComparisons = new ArrayList<>();

    public void classifyComparisons(List<JPlagComparison> comparisons, DatasetType datasetType) {
        double plagiatThreshold;
        double auffaelligThreshold;

        switch (datasetType) {
            case DS19 -> {
                plagiatThreshold = 0.74;
                auffaelligThreshold = 0.60;
            }
            case DS56 -> {
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
            System.out.println("sim: " + sim);
            if (sim >= plagiatThreshold) {
                plagiatComparisons.add(comparison);
                System.out.printf("Plagiat: %.2f\n", sim);
            } else if (sim >= auffaelligThreshold) {
                auffaelligComparisons.add(comparison);
                System.out.printf("Auffällig: %.2f\n", sim);
            } else {
                unauffaelligComparisons.add(comparison);
                System.out.printf("Unauffällig: %.2f\n", sim);
            }
        }
    }

    // Getter
    public List<JPlagComparison> getPlagiatComparisons() {
        return plagiatComparisons;
    }

    public List<JPlagComparison> getAuffaelligComparisons() {
        return auffaelligComparisons;
    }

    public List<JPlagComparison> getUnauffaelligComparisons() {
        return unauffaelligComparisons;
    }
}
