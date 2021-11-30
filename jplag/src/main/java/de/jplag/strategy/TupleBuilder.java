package de.jplag.strategy;

import java.util.ArrayList;
import java.util.List;

import de.jplag.Submission;

/**
 * Class for building tuples of submissions to compare.
 */
public class TupleBuilder {
    private TupleBuilder() {
        // Static class.
    }

    /**
     * @return a list of all submission tuples to be processed.
     */
    public static List<SubmissionTuple> buildComparisonTuples(List<Submission> submissions) {
        List<SubmissionTuple> tuples = new ArrayList<>();
        for (int i = 0; i < (submissions.size() - 1); i++) {
            Submission first = submissions.get(i);
            if (first.getTokenList() != null) {
                for (int j = (i + 1); j < submissions.size(); j++) {
                    Submission second = submissions.get(j);
                    if (second.getTokenList() != null) {
                        tuples.add(new SubmissionTuple(first, second));
                    }
                }
            }
        }
        return tuples;
    }
}
