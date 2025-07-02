package de.jplag.commenthandling;

import java.util.Collections;
import java.util.List;

import de.jplag.*;
import de.jplag.comparison.GreedyStringTiling;
import de.jplag.comparison.TokenValueMapper;
import de.jplag.options.JPlagOptions;

public class CommentComparer {
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

    public List<Match> flipMatches(List<Match> matches) {
        return matches.stream().map(match -> new Match(match.startOfSecond(), match.startOfFirst(), match.endOfSecond(), match.endOfFirst()))
                .toList();
    }

    public JPlagComparison flipComparison(JPlagComparison comparison) {
        return new JPlagComparison(comparison.secondSubmission(), comparison.firstSubmission(), this.flipMatches(comparison.matches()),
                this.flipMatches(comparison.ignoredMatches()), this.flipMatches(comparison.commentMatches()));
    }

    public JPlagResult compareCommentsAndMergeMatches(JPlagResult result) {
        long timeBeforeStartInMillis = System.currentTimeMillis();

        TokenValueMapper tokenValueMapper = new TokenValueMapper(result.getSubmissions(), Submission::getComments);
        GreedyStringTiling algorithm = new GreedyStringTiling(options, tokenValueMapper, Submission::getComments);

        boolean withBaseCode = result.getSubmissions().hasBaseCode();
        if (withBaseCode) {
            compareSubmissionsToBaseCode(result.getSubmissions(), algorithm);
        }

        List<JPlagComparison> fixedComparisons = result.getAllComparisons().stream().parallel().map(oldComparison -> {
            JPlagComparison commentComparison = algorithm.compare(oldComparison.firstSubmission(), oldComparison.secondSubmission());
            if (commentComparison.firstSubmission().equals(oldComparison.secondSubmission())) {
                // Due to internals of the comparison algorithm, the submissions are flipped between code & comment comparisons
                // To ensure that matches are assigned to the correct submission, we're flipping the comment comparison
                commentComparison = this.flipComparison(commentComparison);
            }
            return new JPlagComparison(oldComparison.firstSubmission(), oldComparison.secondSubmission(), oldComparison.matches(),
                    oldComparison.ignoredMatches(), commentComparison.matches());
        }).toList();

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;

        return new JPlagResult(fixedComparisons, result.getSubmissions(), result.getDuration() + durationInMillis, result.getOptions());
    }
}
