package de.jplag.reporting.reportobject.mapper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;

public class SubmissionNameToIdMapper {
    /**
     * Builds a map that associates a Submission by its JPlag Id ({@link Submission#getName()}) to its report viewer id.
     * @return A Map of containing an entry [name of submission -> report viewer id of submission] for each submission of
     * the submission set.
     */
    public static Map<String, String> buildSubmissionNameToIdMap(JPlagResult result) {
        HashMap<String, String> idToName = new HashMap<>();
        getComparisons(result).forEach(comparison -> {
            idToName.put(comparison.getFirstSubmission().getName(), sanitizeNameOf(comparison.getFirstSubmission()));
            idToName.put(comparison.getSecondSubmission().getName(), sanitizeNameOf(comparison.getSecondSubmission()));
        });
        return idToName;
    }

    private static String sanitizeNameOf(Submission comparison) {
        return comparison.getName().replace(File.separator, "_");
    }

    private static List<JPlagComparison> getComparisons(JPlagResult result) {
        int numberOfComparisons = result.getOptions().getMaximumNumberOfComparisons();
        return result.getComparisons(numberOfComparisons);
    }
}
