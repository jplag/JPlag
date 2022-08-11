package de.jplag.reporting.reportobject.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;

public class SubmissionNameToIdMapper {
    /**
     * Gets the names of all submissions.
     * @return A list containing all submission names.
     */
    public static Map<String, String> buildSubmissionNameToIdMap(JPlagResult result) {
        HashMap<String, String> idToName = new HashMap<>();
        getComparisons(result).forEach(comparison -> {
            idToName.put(comparison.getFirstSubmission().getName(), comparison.getFirstSubmission().getNameSanitized());
            idToName.put(comparison.getSecondSubmission().getName(), comparison.getSecondSubmission().getNameSanitized());
        });
        return idToName;
    }

    private static List<JPlagComparison> getComparisons(JPlagResult result) {
        int numberOfComparisons = result.getOptions().getMaximumNumberOfComparisons();
        return result.getComparisons(numberOfComparisons);
    }
}
