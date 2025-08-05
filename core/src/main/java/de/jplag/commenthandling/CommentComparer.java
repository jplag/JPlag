package de.jplag.commenthandling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import de.jplag.*;
import de.jplag.comparison.GreedyStringTiling;
import de.jplag.comparison.TokenSequenceMapper;
import de.jplag.exceptions.ComparisonException;
import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;
import de.jplag.options.JPlagOptions;

/**
 * Implements a parallelized token-based longest common subsequence search for the comments of the submitted programs,
 * based on the implementation of {@code LongestCommonSubsequenceSearch}.
 */
public class CommentComparer {
    private final JPlagOptions options;

    /**
     * Creates a new CommentComparer.
     * @param options JPlag options to use
     */
    public CommentComparer(JPlagOptions options) {
        this.options = options;
    }

    private void compareSubmissionsToBaseCode(SubmissionSet submissions, GreedyStringTiling comparisonAlgorithm) {
        Submission baseCodeSubmission = submissions.getBaseCode();
        for (Submission currentSubmission : submissions.getSubmissions()) {
            JPlagComparison baseCodeCommentComparison = comparisonAlgorithm.generateBaseCodeMarking(currentSubmission, baseCodeSubmission);
            if (currentSubmission.hasBaseCodeComparison()) {
                JPlagComparison oldBaseCodeComparison = currentSubmission.getBaseCodeComparison();

                if (baseCodeCommentComparison.firstSubmission().equals(oldBaseCodeComparison.secondSubmission())) {
                    // Due to internals of the comparison algorithm, the submissions are flipped between code & comment comparisons
                    // To ensure that matches are assigned to the correct submission, we're flipping the comment comparison
                    baseCodeCommentComparison = this.flipComparison(baseCodeCommentComparison);
                }

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

    private List<Match> flipMatches(List<Match> matches) {
        return matches.stream().map(match -> new Match(match.startOfSecond(), match.startOfFirst(), match.lengthOfFirst(), match.lengthOfSecond()))
                .toList();
    }

    private JPlagComparison flipComparison(JPlagComparison comparison) {
        return new JPlagComparison(comparison.secondSubmission(), comparison.firstSubmission(), this.flipMatches(comparison.matches()),
                this.flipMatches(comparison.ignoredMatches()), this.flipMatches(comparison.commentMatches()));
    }

    /**
     * Compares the comments of all submissions inside the supplied result object, adding comment matches to the final
     * comparisons. If basecode is supplied in the result, basecode will also be respected by this function.
     * @param result Result object to add comment matches to
     * @return New result object with all comment matches added
     */
    public JPlagResult compareCommentsAndMergeMatches(JPlagResult result) throws ComparisonException {
        long timeBeforeStartInMillis = System.currentTimeMillis();

        // Preparing data structures
        TokenSequenceMapper tokenValueMapper = new TokenSequenceMapper(result.getSubmissions(), Submission::getComments);
        GreedyStringTiling algorithm = new GreedyStringTiling(options, tokenValueMapper, Submission::getComments);

        // Comparing to base code
        boolean withBaseCode = result.getSubmissions().hasBaseCode();
        if (withBaseCode) {
            compareSubmissionsToBaseCode(result.getSubmissions(), algorithm);
        }

        List<JPlagComparison> fixedComparisons = new ArrayList<>();

        // Comparing comments from pairwise submissions
        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.COMPARING_COMMENTS, result.getAllComparisons().size());
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<JPlagComparison>> futures = result.getAllComparisons().stream().map(oldComparison -> executor.submit(() -> {
                JPlagComparison commentComparison = algorithm.compare(oldComparison.firstSubmission(), oldComparison.secondSubmission());

                if (commentComparison.firstSubmission().equals(oldComparison.secondSubmission())) {
                    // Due to internals of the comparison algorithm, the submissions are flipped between code & comment comparisons
                    // To ensure that matches are assigned to the correct submission, we're flipping the comment comparison
                    commentComparison = this.flipComparison(commentComparison);
                }

                progressBar.step();
                return new JPlagComparison(oldComparison.firstSubmission(), oldComparison.secondSubmission(), oldComparison.matches(),
                        oldComparison.ignoredMatches(), commentComparison.matches());
            })).toList();

            executor.shutdown();
            if (!executor.awaitTermination(24, TimeUnit.HOURS)) {
                throw new ComparisonException("Comment comparison timed out.");
            }

            for (Future<JPlagComparison> future : futures) {
                fixedComparisons.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new ComparisonException("Error during comment comparison algorithm.", e);
        } finally {
            progressBar.dispose();
        }

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;

        return new JPlagResult(fixedComparisons, result.getSubmissions(), result.getDuration() + durationInMillis, result.getOptions());
    }
}
