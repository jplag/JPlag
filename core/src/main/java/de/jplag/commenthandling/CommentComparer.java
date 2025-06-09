package de.jplag.commenthandling;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.comparison.GreedyStringTiling;
import de.jplag.comparison.SubmissionTuple;
import de.jplag.comparison.TokenValueMapper;
import de.jplag.options.JPlagOptions;

public class CommentComparer {
    private final Logger logger = LoggerFactory.getLogger(CommentComparer.class);

    private final JPlagOptions options;

    public CommentComparer(JPlagOptions options) {
        this.options = options;
    }

    private Optional<JPlagComparison> compareSubmissions(GreedyStringTiling algorithm, SubmissionTuple tuple) {
        JPlagComparison comp = algorithm.compare(tuple.left(), tuple.right());
        return Optional.of(comp);
    }

    private List<SubmissionTuple> buildComparisonTuples(List<Submission> submissions) {
        List<SubmissionTuple> tuples = new ArrayList<>();

        for (int i = 0; i < submissions.size() - 1; i++) {
            Submission first = submissions.get(i);
            for (int j = i + 1; j < submissions.size(); j++) {
                Submission second = submissions.get(j);
                if (first.isNew() || second.isNew()) {
                    tuples.add(new SubmissionTuple(first, second));
                }
            }
        }

        return tuples;
    }

    private List<CommentComparison> transformComparisons(List<JPlagComparison> originalComparisons) {
        List<CommentComparison> comparisons = new ArrayList<>();
        for (JPlagComparison originalComparison : originalComparisons) {
            comparisons.add(new CommentComparison(originalComparison));
        }
        return comparisons;
    }

    public void compareCommentsOfSubmissions(SubmissionSet submissionSet) {
        long timeBeforeStartInMillis = System.currentTimeMillis();

        // TODO: Base code?

        TokenValueMapper tokenValueMapper = new TokenValueMapper(submissionSet, Submission::getComments);
        GreedyStringTiling algorithm = new GreedyStringTiling(options, tokenValueMapper, Submission::getComments);

        List<SubmissionTuple> tuples = this.buildComparisonTuples(submissionSet.getSubmissions());

        List<JPlagComparison> comparisons = tuples.stream().parallel().flatMap(tuple -> {
            return this.compareSubmissions(algorithm, tuple).stream();
        }).toList();

        List<CommentComparison> comparisonList = transformComparisons(comparisons);

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;

        logger.info("Comment comparisons in {} ms", durationInMillis);
        for (CommentComparison comparison : comparisonList) {
            logger.info("{}: {}", comparison.toString(), comparison.similarity());
        }
    }
}
