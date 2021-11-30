package de.jplag.strategy;

import java.util.ArrayList;
import java.util.List;

import de.jplag.Submission;
import de.jplag.SubmissionSet;

/**
 * Class for building tuples of submissions to compare.
 */
public class TupleBuilder {
    private TupleBuilder() {
        // Static class.
    }

    /**
     * Compute the comparisons to perform for a number of submission sets.
     * <p>For N root directories the comparison matrix becomes NxN cells where each cell has P_i rows and Q_j columns for
     * the submissions of the root directory. In the upper-right triangle different sets must be compared completely
     * leading to P_i * Q_j comparisons. At the main diagonal, cells should check each set against itself. The bottom-left
     * part of such a cell can be skipped. The bottom-left triangle of the matrix is the mirror of the upper-right triangle,
     * and can be skipped completely.</p>
     * @return Comparisons to perform for completely checking all sets (both against themselves and against all other sets).
     */
    public static List<SubmissionTuple> buildComparisonTuples(List<SubmissionSet> submissionSets) {
        List<SubmissionTuple> tupleCompares = new ArrayList<>();

        int numSets = submissionSets.size();
        for (int i = 0; i < numSets; i++) { // Include the last set for computing intra-set pairs.
            tupleCompares.addAll(buildIntraSetTuples(submissionSets.get(i)));

            for (int j = i + 1; j < numSets; j++) {
                tupleCompares.addAll(buildInterSetTuples(submissionSets.get(i), submissionSets.get(j)));
            }
        }
        return tupleCompares;
    }

    /**
     * Compute the comparisons to perform to check a single set of submissions against itself.
     * @return Submission comparisons required for checking the set.
     */
    private static List<SubmissionTuple> buildIntraSetTuples(SubmissionSet submissionSet) {
        List<Submission> submissions = submissionSet.getSubmissions();
        int numSubmissions = submissions.size();

        List<SubmissionTuple> tuples = new ArrayList<>(numSubmissions * (numSubmissions - 1) / 2);
        for (int i = 0; i < (numSubmissions - 1); i++) {
            Submission first = submissions.get(i);
            if (first.getTokenList() != null) {
                for (int j = (i + 1); j < numSubmissions; j++) {
                    Submission second = submissions.get(j);
                    if (second.getTokenList() != null) {
                        tuples.add(new SubmissionTuple(first, second));
                    }
                }
            }
        }
        return tuples;
    }

    /**
     * Compute the comparisons to perform to check two different sets of submissions against each other.
     * @return Submission comparisons required for checking the sets.
     */
    private static List<SubmissionTuple> buildInterSetTuples(SubmissionSet leftSet, SubmissionSet rightSet) {
        List<Submission> leftSubmissions = leftSet.getSubmissions();
        List<Submission> rightSubmissions = rightSet.getSubmissions();
        int numLeft = leftSubmissions.size();
        int numRight = rightSubmissions.size();

        List<SubmissionTuple> tuples = new ArrayList<>(numLeft * numRight);
        for (Submission leftSubmission: leftSubmissions) {
            if (leftSubmission.getTokenList() != null) {
                for (Submission rightSubmission: rightSubmissions) {
                    if (rightSubmission.getTokenList() != null) {
                        tuples.add(new SubmissionTuple(leftSubmission, rightSubmission));
                    }
                }
            }
        }
        return tuples;
    }

    /**
     * Count the number of submissions in all sets.
     */
    public static int getNumberOfSubmissions(List<SubmissionSet> submissionSets) {
        int totalSubmissions = 0;

        for (SubmissionSet submissionSet: submissionSets) {
            totalSubmissions += submissionSet.numberOfSubmissions();
        }
        return totalSubmissions;
    }
}
