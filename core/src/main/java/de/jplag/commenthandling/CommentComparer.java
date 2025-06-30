package de.jplag.commenthandling;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
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

    private void compareSubmissionsToBaseCode(SubmissionSet submissions, GreedyStringTiling comparisonAlgorithm) {
        Submission baseCodeSubmission = submissions.getBaseCode();
        for (Submission currentSubmission : submissions.getSubmissions()) {
            JPlagComparison baseCodeCommentComparison = comparisonAlgorithm.generateBaseCodeMarking(currentSubmission, baseCodeSubmission);
            if (currentSubmission.hasBaseCodeComparison()) {
                JPlagComparison oldBaseCodeComparison = currentSubmission.getBaseCodeComparison();
                JPlagComparison newBaseCodeComparison = new JPlagComparison(oldBaseCodeComparison.firstSubmission(),
                        oldBaseCodeComparison.secondSubmission(), oldBaseCodeComparison.matches(), oldBaseCodeComparison.ignoredMatches(),
                        baseCodeCommentComparison.matches());
                currentSubmission.setBaseCodeComparison(newBaseCodeComparison);
            } else {
                JPlagComparison newBaseCodeComparison = new JPlagComparison(baseCodeCommentComparison.firstSubmission(),
                        baseCodeCommentComparison.secondSubmission(), Collections.emptyList(), Collections.emptyList(),
                        baseCodeCommentComparison.matches());
                currentSubmission.setBaseCodeComparison(newBaseCodeComparison);
            }
        }
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

        TokenValueMapper tokenValueMapper = new TokenValueMapper(result.getSubmissions(), Submission::getComments);
        GreedyStringTiling algorithm = new GreedyStringTiling(options, tokenValueMapper, Submission::getComments);

        boolean withBaseCode = result.getSubmissions().hasBaseCode();
        if (withBaseCode) {
            compareSubmissionsToBaseCode(result.getSubmissions(), algorithm);
        }

        List<SubmissionTuple> tuples = this.extractComparisonTuples(result.getAllComparisons());

        Map<SubmissionTuple, JPlagComparison> commentComparisons = tuples.stream().parallel().flatMap(tuple -> {
            return this.compareSubmissions(algorithm, tuple).stream().map(comparison -> new AbstractMap.SimpleEntry<>(tuple, comparison));
        }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        List<JPlagComparison> fixedComparisons = new ArrayList<>();

        for (JPlagComparison oldComparison : result.getAllComparisons()) {
            JPlagComparison commentComparison = commentComparisons
                    .get(new SubmissionTuple(oldComparison.firstSubmission(), oldComparison.secondSubmission()));
            if (commentComparison == null) {
                logger.warn("No comment comparison found for: {}", oldComparison);
                fixedComparisons.add(oldComparison);
                continue;
            }

            fixedComparisons.add(new JPlagComparison(oldComparison.firstSubmission(), oldComparison.secondSubmission(), oldComparison.matches(),
                    oldComparison.ignoredMatches(), commentComparison.matches()));
        }

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;

        return new JPlagResult(fixedComparisons, result.getSubmissions(), result.getDuration() + durationInMillis, result.getOptions());
    }
}
