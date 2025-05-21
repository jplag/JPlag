package de.jplag.reporting.reportobject.model;

import java.util.List;

public record RunInformation(Version version,

        List<String> failedSubmissionNames,

        String dateOfExecution,

        long executionTime,

        int totalComparisons) {
}
