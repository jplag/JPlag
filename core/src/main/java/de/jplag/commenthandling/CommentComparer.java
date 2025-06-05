package de.jplag.commenthandling;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.comparison.SubmissionTuple;
import de.jplag.options.JPlagOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommentComparer {
    private final Logger logger = LoggerFactory.getLogger(CommentComparer.class);

    private final JPlagOptions options;

    public CommentComparer(JPlagOptions options) {
        this.options = options;
    }

    private Optional<JPlagComparison> compareSubmissions(GreedyStringTilingForComments algorithm, SubmissionTuple tuple) {
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

    public void compareCommentsOfSubmissions(SubmissionSet submissionSet) {
        long timeBeforeStartInMillis = System.currentTimeMillis();

        // TODO: Base code?

        TokenValueMapperForComments tokenValueMapper = new TokenValueMapperForComments(submissionSet);
        GreedyStringTilingForComments algorithm = new GreedyStringTilingForComments(options, tokenValueMapper);

        List<SubmissionTuple> tuples = this.buildComparisonTuples(submissionSet.getSubmissions());

        List<JPlagComparison> comparisons = tuples.stream().parallel().flatMap(tuple -> {
            return this.compareSubmissions(algorithm, tuple).stream();
        }).toList();

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;

        logger.info("Comment comparisons in {} ms", durationInMillis);
        for (JPlagComparison comparison : comparisons) {
            logger.info("{}: {}", comparison.toString(), comparison.similarity());
        }
    }
}
