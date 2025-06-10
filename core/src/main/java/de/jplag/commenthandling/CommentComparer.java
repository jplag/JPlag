package de.jplag.commenthandling;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
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

    private List<SubmissionTuple> extractComparisonTuples(List<JPlagComparison> comparisons) {
        List<SubmissionTuple> tuples = new ArrayList<>();

        for (JPlagComparison comparison : comparisons) {
            tuples.add(new SubmissionTuple(comparison.firstSubmission(), comparison.secondSubmission()));
        }

        return tuples;
    }

    public JPlagResult compareCommentsAndMergeMatches(JPlagResult result) {
        long timeBeforeStartInMillis = System.currentTimeMillis();

        // TODO: Base code?

        TokenValueMapper tokenValueMapper = new TokenValueMapper(result.getSubmissions(), Submission::getComments);
        GreedyStringTiling algorithm = new GreedyStringTiling(options, tokenValueMapper, Submission::getComments);

        List<SubmissionTuple> tuples = this.extractComparisonTuples(result.getAllComparisons());

        Map<SubmissionTuple, JPlagComparison> commentComparisons = tuples.stream().parallel().flatMap(tuple -> {
            return this.compareSubmissions(algorithm, tuple).stream().map(comparison -> new AbstractMap.SimpleEntry<>(tuple, comparison));
        }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        for (JPlagComparison oldComparison : result.getAllComparisons()) {
            JPlagComparison commentComparison = commentComparisons
                    .get(new SubmissionTuple(oldComparison.firstSubmission(), oldComparison.secondSubmission()));
            if (commentComparison == null) {
                logger.warn("No comment comparison found for: {}", oldComparison);
                continue;
            }

            // TODO
        }

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;

        return new JPlagResult(result.getAllComparisons(), // TODO
                result.getSubmissions(), result.getDuration() + durationInMillis, result.getOptions());
    }
}
