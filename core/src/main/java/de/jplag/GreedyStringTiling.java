package de.jplag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.jplag.options.JPlagOptions;

/**
 * This class implements the Greedy String Tiling algorithm as introduced by Michael Wise. However, it is very specific
 * to the classes {@link Token}, and {@link Match}. Class implementation is thread-safe, i.e. submission can be compared
 * in parallel.
 * @see <a href=
 * "https://www.researchgate.net/publication/262763983_String_Similarity_via_Greedy_String_Tiling_and_Running_Karp-Rabin_Matching">
 * String Similarity via Greedy String Tiling and Running Karp−Rabin Matching </a>
 */
public class GreedyStringTiling {

    private final int minimumMatchLength;
    private ConcurrentMap<TokenType, Integer> tokenTypeValues;
    private final Map<Submission, Set<Token>> baseCodeMarkings = new IdentityHashMap<>();

    private final Map<Submission, int[]> cachedTokenValueLists = new IdentityHashMap<>();
    private final Map<Submission, SubsequenceHashLookupTable> cachedHashLookupTables = new IdentityHashMap<>();

    public GreedyStringTiling(JPlagOptions options) {
        this.minimumMatchLength = options.minimumTokenMatch();
        this.tokenTypeValues = new ConcurrentHashMap<>();
        this.tokenTypeValues.put(SharedTokenType.FILE_END, 0);
    }

    /**
     * Compares the given submission with the base code submission. Marks the identified base code sections in the
     * submission such that further comparisons do not generate matches for these parts. Must be called before generating a
     * comparison with a regular submission for the given submission.
     * @param submission is the submission to generate base-code markings for.
     * @param baseCodeSubmission is the base code submission.
     * @return the comparison of the submission with the base code submission.
     */
    public final JPlagComparison generateBaseCodeMarking(Submission submission, Submission baseCodeSubmission) {
        JPlagComparison comparison = compare(submission, baseCodeSubmission);

        List<Token> submissionTokenList = submission.getTokenList();
        Set<Token> baseCodeMarking = new HashSet<>();
        for (Match match : comparison.matches()) {
            int startIndex = comparison.firstSubmission() == submission ? match.startOfFirst() : match.startOfSecond();
            baseCodeMarking.addAll(submissionTokenList.subList(startIndex, startIndex + match.length()));
        }
        baseCodeMarkings.put(submission, baseCodeMarking);

        // Remove the lookup table for the current submission to trigger a regeneration as hashes will change due to the new
        // baseCodeMarking.
        // This is a performance optimization to not suggest subsequences with baseCode for the matching.
        // Removing this optimization would not change the result as the baseCode matches are additionally checked by validating
        // that no match has a marked token (which baseCode-containing tokens are).
        cachedHashLookupTables.remove(submission);

        return comparison;
    }

    /**
     * Compares the two submissions and generates matches between them. To exclude base code from the result, call
     * {@link #generateBaseCodeMarking} with each submission beforehand.
     * @param firstSubmission is one of the two submissions.
     * @param secondSubmission is the other of the two submissions.
     * @return the comparison between the two submissions.
     */
    public final JPlagComparison compare(Submission firstSubmission, Submission secondSubmission) {
        Submission smallerSubmission;
        Submission largerSubmission;
        if (firstSubmission.getTokenList().size() > secondSubmission.getTokenList().size()) {
            smallerSubmission = secondSubmission;
            largerSubmission = firstSubmission;
        } else {
            smallerSubmission = firstSubmission;
            largerSubmission = secondSubmission;
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
        List<Token> leftTokens = leftSubmission.getTokenList();
        List<Token> rightTokens = rightSubmission.getTokenList();

        int[] leftValues = tokenValueListFromSubmission(leftSubmission);
        int[] rightValues = tokenValueListFromSubmission(rightSubmission);

        // comparison uses <= because it is assumed that the last token is a pivot (FILE_END)
        if (leftTokens.size() <= minimumMatchLength || rightTokens.size() <= minimumMatchLength) {
            return new JPlagComparison(leftSubmission, rightSubmission, List.of());
        }

        Set<Integer> leftMarkedIndexes = initiallyMarkedTokenIndexes(leftSubmission);
        Set<Integer> rightMarkedIndexes = initiallyMarkedTokenIndexes(rightSubmission);

        SubsequenceHashLookupTable leftLookupTable = subsequenceHashLookupTableForSubmission(leftSubmission, leftMarkedIndexes);
        SubsequenceHashLookupTable rightLookupTable = subsequenceHashLookupTableForSubmission(rightSubmission, rightMarkedIndexes);

        int maximumMatchLength;
        List<Match> globalMatches = new ArrayList<>();
        do {
            maximumMatchLength = minimumMatchLength;
            List<Match> iterationMatches = new ArrayList<>();
            for (int leftStartIndex = 0; leftStartIndex < leftValues.length - maximumMatchLength; leftStartIndex++) {
                int leftSubsequenceHash = leftLookupTable.subsequenceHashForStartIndex(leftStartIndex);
                if (leftMarkedIndexes.contains(leftStartIndex) || leftSubsequenceHash == SubsequenceHashLookupTable.NO_HASH) {
                    continue;
                }
                List<Integer> possiblyMatchingRightStartIndexes = rightLookupTable
                        .startIndexesOfPossiblyMatchingSubsequencesForSubsequenceHash(leftSubsequenceHash);
                for (Integer rightStartIndex : possiblyMatchingRightStartIndexes) {
                    // comparison uses >= because it is assumed that the last token is a pivot (FILE_END)
                    if (rightMarkedIndexes.contains(rightStartIndex) || maximumMatchLength >= rightValues.length - rightStartIndex) {
                        continue;
                    }

                    int subsequenceMatchLength = maximalMatchingSubsequenceLengthNotMarked(leftValues, leftStartIndex, leftMarkedIndexes, rightValues,
                            rightStartIndex, rightMarkedIndexes, maximumMatchLength);
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
                addMatchIfNotOverlapping(globalMatches, match);
                int leftStartIndex = match.startOfFirst();
                int rightStartIndex = match.startOfSecond();
                for (int offset = 0; offset < match.length(); offset++) {
                    leftMarkedIndexes.add(leftStartIndex + offset);
                    rightMarkedIndexes.add(rightStartIndex + offset);
                }
            }
        } while (maximumMatchLength != minimumMatchLength);
        return new JPlagComparison(leftSubmission, rightSubmission, globalMatches);
    }

    /**
     * Computes the maximal matching subsequence between the two lists starting at their respective indexes. Values are
     * matching if they are equal and not marked. Comparison is performed backwards for the minimum sequence length based on
     * the assumption that the further tokens are away, the more likely they differ.
     * @param leftValues The list of left values.
     * @param leftStartIndex The start index in the left list.
     * @param leftMarkedIndexes The marked indexes of the left list.
     * @param rightValues The list of right values.
     * @param rightStartIndex The start index in the right list.
     * @param rightMarkedIndexes The marked indexes of the right list.
     * @param minimumSequenceLength The minimal sequence length for a matching subsequence. Must be not negative.
     * @return the maximal matching subsequence length, or 0 if there is no subsequence of at least the minimum sequence
     * length.
     */
    private int maximalMatchingSubsequenceLengthNotMarked(int[] leftValues, int leftStartIndex, Set<Integer> leftMarkedIndexes, int[] rightValues,
            int rightStartIndex, Set<Integer> rightMarkedIndexes, int minimumSequenceLength) {
        for (int offset = minimumSequenceLength - 1; offset >= 0; offset--) {
            int leftIndex = leftStartIndex + offset;
            int rightIndex = rightStartIndex + offset;
            if (leftValues[leftIndex] != rightValues[rightIndex] || leftMarkedIndexes.contains(leftIndex)
                    || rightMarkedIndexes.contains(rightIndex)) {
                return 0;
            }
        }
        int offset = minimumSequenceLength;
        while (leftValues[leftStartIndex + offset] == rightValues[rightStartIndex + offset] && !leftMarkedIndexes.contains(leftStartIndex + offset)
                && !rightMarkedIndexes.contains(rightStartIndex + offset)) {
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

    private Set<Integer> initiallyMarkedTokenIndexes(Submission submission) {
        Set<Token> baseCodeTokens = baseCodeMarkings.get(submission);
        List<Token> tokens = submission.getTokenList();
        return IntStream.range(0, tokens.size())
                .filter(i -> tokens.get(i).getType().isExcludedFromMatching() || (baseCodeTokens != null && baseCodeTokens.contains(tokens.get(i))))
                .boxed().collect(Collectors.toSet());
    }

    private SubsequenceHashLookupTable subsequenceHashLookupTableForSubmission(Submission submission, Set<Integer> markedIndexes) {
        return cachedHashLookupTables.computeIfAbsent(submission,
                (key -> new SubsequenceHashLookupTable(minimumMatchLength, tokenValueListFromSubmission(key), markedIndexes)));
    }

    /**
     * Converts the tokens of the submission to a list of values.
     * @param submission The submission from which to convert the tokens.
     */
    private int[] tokenValueListFromSubmission(Submission submission) {
        return cachedTokenValueLists.computeIfAbsent(submission, (key -> {
            List<Token> tokens = key.getTokenList();
            int[] tokenValueList = new int[tokens.size()];
            for (int i = 0; i < tokens.size(); i++) {
                TokenType type = tokens.get(i).getType();
                synchronized (tokenTypeValues) {
                    tokenTypeValues.putIfAbsent(type, tokenTypeValues.size());
                }
                tokenValueList[i] = tokenTypeValues.get(type);
            }
            return tokenValueList;
        }));
    }
}
