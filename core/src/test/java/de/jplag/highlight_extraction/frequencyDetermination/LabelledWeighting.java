package de.jplag.highlight_extraction.frequencyDetermination;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.highlight_extraction.FrequencySimilarity;

import java.util.*;

/**
 * Diese Klasse übernimmt die Gruppierung von JPlagComparisons
 * in die Kategorien: Plagiat, Auffällig, Unauffällig und ZwischenGruppen.
 * Die Einteilung erfolgt anhand der höchsten Similarity je Submission
 * (ohne Frequency-Gewichtung).
 */
public class LabelledWeighting {

    private final Map<Submission, Double> maxSimilarityMap = new HashMap<>();
    private final Map<Submission, String> submissionCategory = new HashMap<>();

    private final List<JPlagComparison> plagiatComparisons = new ArrayList<>();
    private final List<JPlagComparison> auffaelligComparisons = new ArrayList<>();
    private final List<JPlagComparison> unauffaelligComparisons = new ArrayList<>();
    private final List<JPlagComparison> zwischenGruppenComparisons = new ArrayList<>();

    public void classifyComparisons0(List<JPlagComparison> comparisons, FrequencySimilarity similarityCalculator) {
        calculateMaxSimilarities0(comparisons, similarityCalculator);
        categorizeSubmissions();
        groupComparisons(comparisons);
    }

    private void calculateMaxSimilarities0(List<JPlagComparison> comparisons, FrequencySimilarity similarityCalculator) {
        for (JPlagComparison comparison : comparisons) {
            Submission s1 = comparison.firstSubmission();
            Submission s2 = comparison.secondSubmission();
                double sim = comparison.similarity(); // alte gewichtung
            maxSimilarityMap.merge(s1, sim, Math::max);
            maxSimilarityMap.merge(s2, sim, Math::max);
        }
    }

    private void categorizeSubmissions() {
        for (Map.Entry<Submission, Double> entry : maxSimilarityMap.entrySet()) {
            Submission submission = entry.getKey();
            double sim = entry.getValue();
            if (sim >= 0.71) {
                submissionCategory.put(submission, "Plagiat");
                System.out.printf("Plagiat: " + sim);
            } else if (sim >= 0.51) {
                submissionCategory.put(submission, "Auffällig");
                System.out.printf("Auffällig: " + sim);
            } else {
                submissionCategory.put(submission, "Unauffällig");
                System.out.printf("Unauffällig: " + sim);
            }
        }
    }

    private void groupComparisons(List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            Submission s1 = comparison.firstSubmission();
            Submission s2 = comparison.secondSubmission();

            String cat1 = submissionCategory.get(s1);
            String cat2 = submissionCategory.get(s2);
            if (cat1 == null || cat2 == null) continue;
            if (cat1.equals(cat2)) {
                switch (cat1) {
                    case "Plagiat" -> {
                        if(comparison.similarity() >= 0.71) {plagiatComparisons.add(comparison);}
                    }
                    case "Auffällig" -> {
                        if(comparison.similarity() >= 0.51) {auffaelligComparisons.add(comparison);}
                    }
                    case "Unauffällig" -> unauffaelligComparisons.add(comparison);
                }
            } else {
                zwischenGruppenComparisons.add(comparison);
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
