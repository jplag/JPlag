package de.jplag.commenthandling;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.Token;
import de.jplag.options.JPlagOptions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements the Greedy String Tiling algorithm as introduced by Michael Wise. However, it is very specific
 * to the classes {@link Token}, and {@link Match}. Class implementation is thread-safe, i.e. submission can be compared
 * in parallel.
 * @see <a href=
 * "https://www.researchgate.net/publication/262763983_String_Similarity_via_Greedy_String_Tiling_and_Running_Karp-Rabin_Matching">
 * String Similarity via Greedy String Tiling and Running Karp−Rabin Matching </a>
 */
public class GreedyStringTilingForComments {
    private final int minimumMatchLength;
    private final JPlagOptions options;
    private final Map<Submission, Set<Token>> baseCodeMarkings = new IdentityHashMap<>();

    private final Map<Submission, SubsequenceHashLookupTableForComments> cachedHashLookupTables = Collections.synchronizedMap(new IdentityHashMap<>());

    private final TokenValueMapperForComments tokenValueMapper;

    private static final String ERROR_INDEX_OUT_OF_BOUNDS = """
                GST index out of bounds. This is probably a random issue caused by multithreading issues.
                Length of the list that caused the exception (the list of marks for the relevant submission): %s, Index in that list: %s
                TokenCount: %s, TokenList: %s
                CachedTokenCount: %s
                Submission (cause of error): %s
                Submission (other): %s
            """.trim().stripIndent();

    public GreedyStringTilingForComments(JPlagOptions options, TokenValueMapperForComments tokenValueMapper) {
        this.options = options;
        // Ensures 1 <= neighborLength <= minimumTokenMatch
        int minimumNeighborLength = Math.clamp(options.mergingOptions().minimumNeighborLength(), 1, options.minimumTokenMatch());

        this.minimumMatchLength = options.mergingOptions().enabled() ? minimumNeighborLength : options.minimumTokenMatch();

        this.tokenValueMapper = tokenValueMapper;
    }

    /**
     * Compares the two submissions in a thread-safe manner and generates matches between them.
     * @param firstSubmission is one of the two submissions.
     * @param secondSubmission is the other of the two submissions.
     * @return the comparison between the two submissions.
     */
    public final JPlagComparison compare(Submission firstSubmission, Submission secondSubmission) {
        Submission smallerSubmission;
        Submission largerSubmission;
        Comparator<Submission> submissionComparator = Comparator.comparing((Submission it) -> it.getComments().size())
                .thenComparing(Submission::getName);

        if (submissionComparator.compare(firstSubmission, secondSubmission) <= 0) {
            smallerSubmission = firstSubmission;
            largerSubmission = secondSubmission;
        } else {
            smallerSubmission = secondSubmission;
            largerSubmission = firstSubmission;
        }
        return compareInternal(smallerSubmission, largerSubmission);
    }

    /**
     * Compares two submissions. FILE_END is used as pivot
     * @param leftSubmission is the submission with the smaller sequence.
     * @param rightSubmission is the submission with the larger sequence.
     * @return the comparison results.
     */
    private JPlagComparison compareInternal(Submission leftSubmission, Submission rightSubmission) {
        int[] leftValues = this.tokenValueMapper.getTokenValuesFor(leftSubmission);
        int[] rightValues = this.tokenValueMapper.getTokenValuesFor(rightSubmission);

        boolean[] leftMarked = calculateInitiallyMarked(leftSubmission);
        boolean[] rightMarked = calculateInitiallyMarked(rightSubmission);

        SubsequenceHashLookupTableForComments leftLookupTable = subsequenceHashLookupTableForSubmission(leftSubmission, leftMarked);
        SubsequenceHashLookupTableForComments rightLookupTable = subsequenceHashLookupTableForSubmission(rightSubmission, rightMarked);

        int maximumMatchLength;
        List<Match> globalMatches = new ArrayList<>();
        List<Match> ignoredMatches = new ArrayList<>();
        do {
            maximumMatchLength = minimumMatchLength;
            List<Match> iterationMatches = new ArrayList<>();
            for (int leftStartIndex = 0; leftStartIndex < leftValues.length - maximumMatchLength; leftStartIndex++) {
                int leftSubsequenceHash = leftLookupTable.subsequenceHashForStartIndex(leftStartIndex);
                if (checkMark(leftMarked, leftStartIndex, leftSubmission, rightSubmission)
                        || leftSubsequenceHash == SubsequenceHashLookupTableForComments.NO_HASH) {
                    continue;
                }
                List<Integer> possiblyMatchingRightStartIndexes = rightLookupTable
                        .startIndexesOfPossiblyMatchingSubsequencesForSubsequenceHash(leftSubsequenceHash);
                for (Integer rightStartIndex : possiblyMatchingRightStartIndexes) {
                    // comparison uses >= because it is assumed that the last token is a pivot (FILE_END)
                    if (checkMark(rightMarked, rightStartIndex, rightSubmission, leftSubmission)
                            || maximumMatchLength >= rightValues.length - rightStartIndex) {
                        continue;
                    }

                    int subsequenceMatchLength = maximalMatchingSubsequenceLengthNotMarked(leftValues, leftStartIndex, leftMarked, rightValues,
                            rightStartIndex, rightMarked, maximumMatchLength);
                    if (subsequenceMatchLength >= maximumMatchLength) {
                        if (subsequenceMatchLength > maximumMatchLength) {
                            iterationMatches.clear();
                            maximumMatchLength = subsequenceMatchLength;
                        }
                        Match match = new Match(leftStartIndex, rightStartIndex, subsequenceMatchLength);
                        addMatchIfNotOverlapping(iterationMatches, match);
                    }
                }
            }
            for (Match match : iterationMatches) {
                if (match.length() < options.minimumTokenMatch()) {
                    addMatchIfNotOverlapping(ignoredMatches, match);
                } else {
                    addMatchIfNotOverlapping(globalMatches, match);
                }
                int leftStartIndex = match.startOfFirst();
                int rightStartIndex = match.startOfSecond();
                for (int offset = 0; offset < match.length(); offset++) {
                    leftMarked[leftStartIndex + offset] = true;
                    rightMarked[rightStartIndex + offset] = true;
                }
            }
        } while (maximumMatchLength != minimumMatchLength);
        return new JPlagComparison(leftSubmission, rightSubmission, globalMatches, ignoredMatches);
    }

    /**
     * Computes the maximal matching subsequence between the two lists starting at their respective indexes. Values are
     * matching if they are equal and not marked. Comparison is performed backwards for the minimum sequence length based on
     * the assumption that the further tokens are away, the more likely they differ.
     * @param leftValues The list of left values.
     * @param leftStartIndex The start index in the left list.
     * @param leftMarked Which left values are marked.
     * @param rightValues The list of right values.
     * @param rightStartIndex The start index in the right list.
     * @param rightMarked Which right values are marked.
     * @param minimumSequenceLength The minimal sequence length for a matching subsequence. Must be not negative.
     * @return the maximal matching subsequence length, or 0 if there is no subsequence of at least the minimum sequence
     * length.
     */
    private int maximalMatchingSubsequenceLengthNotMarked(int[] leftValues, int leftStartIndex, boolean[] leftMarked, int[] rightValues,
            int rightStartIndex, boolean[] rightMarked, int minimumSequenceLength) {
        for (int offset = minimumSequenceLength - 1; offset >= 0; offset--) {
            int leftIndex = leftStartIndex + offset;
            int rightIndex = rightStartIndex + offset;
            if (leftValues[leftIndex] != rightValues[rightIndex] || leftMarked[leftIndex] || rightMarked[rightIndex]) {
                return 0;
            }
        }
        int offset = minimumSequenceLength;
        while (leftValues[leftStartIndex + offset] == rightValues[rightStartIndex + offset] && !leftMarked[leftStartIndex + offset]
                && !rightMarked[rightStartIndex + offset]) {
            offset++;
        }
        return offset;
    }

    private void addMatchIfNotOverlapping(List<Match> matches, Match match) {
        for (int i = matches.size() - 1; i >= 0; i--) { // starting at the end is better(?)
            if (matches.get(i).overlaps(match)) {
                return; // no overlaps allowed!
            }
        }
        matches.add(match);
    }

    private boolean[] calculateInitiallyMarked(Submission submission) {
        Set<Token> baseCodeTokens = baseCodeMarkings.get(submission);
        List<Token> tokens = submission.getComments();
        boolean[] result = new boolean[tokens.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = tokens.get(i).getType().isExcludedFromMatching() || baseCodeTokens != null && baseCodeTokens.contains(tokens.get(i));
        }
        return result;
    }

    private SubsequenceHashLookupTableForComments subsequenceHashLookupTableForSubmission(Submission submission, boolean[] marked) {
        return cachedHashLookupTables.computeIfAbsent(submission,
                key -> new SubsequenceHashLookupTableForComments(minimumMatchLength, this.tokenValueMapper.getTokenValuesFor(submission), marked));
    }

    private boolean checkMark(boolean[] marks, int index, Submission submission, Submission otherSubmission) {
        if (index >= marks.length) {
            throw new IllegalStateException(String.format(ERROR_INDEX_OUT_OF_BOUNDS, marks.length, index, submission.getComments().size(),
                    submission.getComments().stream().map(it -> it.getType().getDescription()).collect(Collectors.joining(", ")),
                    this.tokenValueMapper.getTokenValuesFor(submission).length, submission.getName(), otherSubmission.getName()));
        }

        return marks[index];
    }
}
