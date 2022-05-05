package de.jplag.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.GreedyStringTiling;
import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.options.JPlagOptions;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GreedyStringTiling greedyStringTiling;

    protected JPlagOptions options;

    public AbstractComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
        this.greedyStringTiling = greedyStringTiling;
        this.options = options;
    }

    /**
     * Compare all submissions to the basecode.
     * <p>
     * Caller must ensure that the provided set does have a basecode submission before calling.
     * </p>
     * @param submissionSet Submissions and basecode to compare.
     */
    protected void compareSubmissionsToBaseCode(SubmissionSet submissionSet) {
        Submission baseCodeSubmission = submissionSet.getBaseCode();
        for (Submission currentSubmission : submissionSet.getSubmissions()) {
            JPlagComparison baseCodeComparison = greedyStringTiling.compareWithBaseCode(currentSubmission, baseCodeSubmission);
            currentSubmission.setBaseCodeComparison(baseCodeComparison);
            baseCodeSubmission.resetBaseCode();
        }
    }

    /**
     * Compares two submissions and optionally returns the results if similarity is high enough.
     */
    protected Optional<JPlagComparison> compareSubmissions(Submission first, Submission second, boolean withBaseCode) {
        JPlagComparison comparison = greedyStringTiling.compare(first, second);
        logger.info("Comparing " + first.getName() + "-" + second.getName() + ": " + comparison.similarity());

        if (options.getSimilarityMetric().isAboveThreshold(comparison, options.getSimilarityThreshold())) {
            return Optional.of(comparison);
        }
        return Optional.empty();
    }

    /**
     * @return a list of all submission tuples to be processed.
     */
    protected static List<SubmissionTuple> buildComparisonTuples(List<Submission> submissions) {
        List<SubmissionTuple> tuples = new ArrayList<>();

        for (int i = 0; i < (submissions.size() - 1); i++) {
            Submission first = submissions.get(i);
            if (first.getTokenList() == null) {
                continue;
            }

            for (int j = (i + 1); j < submissions.size(); j++) {
                Submission second = submissions.get(j);
                if (second.getTokenList() == null) {
                    continue;
                }

                if (first.isNew() || second.isNew()) {
                    tuples.add(new SubmissionTuple(first, second));
                }
            }
        }
        return tuples;
    }
}
