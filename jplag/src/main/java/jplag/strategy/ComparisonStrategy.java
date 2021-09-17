package jplag.strategy;

import java.util.ArrayList;

import jplag.JPlagResult;
import jplag.Submission;

/**
 * Strategy for comparing a set of submissions.
 */
public interface ComparisonStrategy {

    /**
     * Compares submissions from a set of submissions while considering a given base code.
     * @param submissions is the set of submissions.
     * @param baseCodeSubmission is the base code on which each submission is based on.
     * @return the comparison results.
     */
    JPlagResult compareSubmissions(ArrayList<Submission> submissions, Submission baseCodeSubmission);
}
