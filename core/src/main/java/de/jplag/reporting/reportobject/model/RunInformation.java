package de.jplag.reporting.reportobject.model;

import java.util.List;

public record RunInformation(Version version,

        List<FailedSubmission> failedSubmissions,

        String dateOfExecution,

        long executionTime,

        int totalComparisons) {
}
