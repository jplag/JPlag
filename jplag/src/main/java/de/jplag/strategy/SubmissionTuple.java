package de.jplag.strategy;

import de.jplag.Submission;

/**
 * Tupel of source code submissions.
 */
public class SubmissionTuple {
    private final Submission left;
    private final Submission right;

    /**
     * Creates a tupel from two submissions.
     */
    public SubmissionTuple(Submission left, Submission right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Submissions cannot be null");
        }
        this.left = left;
        this.right = right;
    }

    public Submission getLeft() {
        return left;
    }

    public Submission getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + " | " + right + ")";
    }
}
