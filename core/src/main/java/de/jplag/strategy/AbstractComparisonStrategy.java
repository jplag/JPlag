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

    protected final JPlagOptions options;

    protected AbstractComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
        this.greedyStringTiling = greedyStringTiling;
        this.options = options;
        logger.info("Start comparing...");
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
            JPlagComparison baseCodeComparison = greedyStringTiling.generateBaseCodeMarking(currentSubmission, baseCodeSubmission);
            currentSubmission.setBaseCodeComparison(baseCodeComparison);
        }
    }

    /**
     * Compares two submissions and optionally returns the results if similarity is high enough.
     */
    protected Optional<JPlagComparison> compareSubmissions(Submission first, Submission second) {
        JPlagComparison comparison = greedyStringTiling.compare(first, second);
        logger.info("Comparing {}-{}: {}", first.getName(), second.getName(), comparison.similarity());

        if (options.similarityMetric().isAboveThreshold(comparison, options.similarityThreshold())) {
            return Optional.of(comparison);
        }
        return Optional.empty();
    }

    /**
     * @return a list of all submission tuples to be processed.
     */
    protected static List<SubmissionTuple> buildComparisonTuples(List<Submission> submissions) {
        List<SubmissionTuple> tuples = new ArrayList<>();
        List<Submission> validSubmissions = submissions.stream().filter(s -> s.getTokenList() != null).toList();

        for (int i = 0; i < (validSubmissions.size() - 1); i++) {
            Submission first = validSubmissions.get(i);
            for (int j = (i + 1); j < validSubmissions.size(); j++) {
                Submission second = validSubmissions.get(j);
                if (first.isNew() || second.isNew()) {
                    tuples.add(new SubmissionTuple(first, second));
                }
            }
        }
        return tuples;
    }
}
