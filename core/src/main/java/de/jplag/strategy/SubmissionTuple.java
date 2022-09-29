package de.jplag.strategy;

import de.jplag.Submission;

/**
 * Tuple of source code submissions.
 */
public record SubmissionTuple(Submission left, Submission right) {
    /**
     * Creates a tuple from two submissions.
     */
    public SubmissionTuple {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Submissions cannot be null");
        }
    }

    @Override
    public String toString() {
        return "(" + left + " | " + right + ")";
    }
}
