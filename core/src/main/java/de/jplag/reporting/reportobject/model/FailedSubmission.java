package de.jplag.reporting.reportobject.model;

import de.jplag.SubmissionState;

/**
 * Represents a failed submission with its ID and the associated submission state.
 * @param submissionId the unique identifier of the submission
 * @param submissionState the state of the submission indicating the failure reason
 */
public record FailedSubmission(String submissionId, SubmissionState submissionState) {
}
