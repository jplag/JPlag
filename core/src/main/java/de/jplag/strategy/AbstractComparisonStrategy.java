package de.jplag.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.GreedyStringTiling;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;
import de.jplag.options.JPlagOptions;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

    private final Logger logger = LoggerFactory.getLogger(ComparisonStrategy.class);

    private final GreedyStringTiling greedyStringTiling;

    private final JPlagOptions options;

    protected AbstractComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
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
            JPlagComparison baseCodeComparison = greedyStringTiling.generateBaseCodeMarking(currentSubmission, baseCodeSubmission);
            currentSubmission.setBaseCodeComparison(baseCodeComparison);
        }
    }

    /**
     * Compares two submissions and optionally returns the results if similarity is high enough.
     */
    protected Optional<JPlagComparison> compareSubmissions(Submission first, Submission second) {
        JPlagComparison comparison = greedyStringTiling.compare(first, second);
        logger.trace("Comparing {}-{}: {}", first.getName(), second.getName(), comparison.similarity());

        if (options.similarityMetric().isAboveThreshold(comparison, options.similarityThreshold())) {
            return Optional.of(comparison);
        }
        return Optional.empty();
    }

    /**
     * @return a list of all submission tuples to be processed.
     */
    private List<SubmissionTuple> buildComparisonTuples(List<Submission> submissions) {
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

    @Override
    public JPlagResult compareSubmissions(SubmissionSet submissionSet) {
        long timeBeforeStartInMillis = System.currentTimeMillis();

        handleBaseCode(submissionSet);

        List<SubmissionTuple> tuples = buildComparisonTuples(submissionSet.getSubmissions());
        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.COMPARING, tuples.size());
        List<JPlagComparison> comparisons = prepareStream(tuples).flatMap(tuple -> {
            Optional<JPlagComparison> result = compareTuple(tuple);
            progressBar.step();
            return result.stream();
        }).toList();
        progressBar.dispose();

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;

        return new JPlagResult(comparisons, submissionSet, durationInMillis, options);
    }

    /**
     * Handle the parsing of the base code.
     * @param submissionSet The submission set to parse
     */
    protected abstract void handleBaseCode(SubmissionSet submissionSet);

    /**
     * Prepare a stream for parsing the tuples. Here you can modify the tuples or the stream as necessary.
     * @param tuples The tuples to stream
     * @return The Stream of tuples
     */
    protected abstract Stream<SubmissionTuple> prepareStream(List<SubmissionTuple> tuples);

    /**
     * Compares a single tuple. Returns nothing, if the similarity is not high enough.
     * @param tuple The Tuple to compare
     * @return The comparison
     */
    protected abstract Optional<JPlagComparison> compareTuple(SubmissionTuple tuple);
}
