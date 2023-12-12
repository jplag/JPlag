package de.jplag.strategy;

import de.jplag.JPlagResult;
import de.jplag.SubmissionSet;
import de.jplag.UiHooks;

/**
 * Strategy for comparing a set of submissions.
 */
public interface ComparisonStrategy {

    /**
     * Compares submissions from a set of submissions while considering a given base code.
     * @param submissionSet Collection of submissions with optional basecode to compare.
     * @param uiHooks Used to notify the ui of state changes
     * @return the comparison results.
     */
    JPlagResult compareSubmissions(SubmissionSet submissionSet, UiHooks uiHooks);
}
