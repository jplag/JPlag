package de.jplag.reporting.reportobject.model;

import de.jplag.SubmissionState;

public record FailedSubmission(String submissionId, SubmissionState submissionState) {
}
