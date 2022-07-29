package de.jplag;

import static de.jplag.TokenConstants.FILE_END;
import static de.jplag.TokenConstants.SEPARATOR_TOKEN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.jplag.options.JPlagOptions;

/**
 * This class implements the Greedy String Tiling algorithm as introduced by Michael Wise. However, it is very specific
 * to the classes {@link Token}, and {@link Match}. While this class was reworked, it still contains some quirks from
 * the initial version.
 * @see <a href=
 * "https://www.researchgate.net/publication/262763983_String_Similarity_via_Greedy_String_Tiling_and_Running_Karp-Rabin_Matching">
 * String Similarity via Greedy String Tiling and Running Karp−Rabin Matching </a>
 */
public class GreedyStringTiling {

    private final JPlagOptions options;
    private Map<Submission, SubsequenceHashLookupTable> hashLookupTables = new HashMap<>();

    public GreedyStringTiling(JPlagOptions options) {
        this.options = options;
    }

    public final JPlagComparison compare(Submission firstSubmission, Submission secondSubmission) {
        return swapAndCompare(firstSubmission, secondSubmission);
    }

    public final JPlagComparison compareWithBaseCode(Submission submission, Submission baseCodeSubmission) {
        JPlagComparison comparison = swapAndCompare(submission, baseCodeSubmission);
        // Remove the hashLookupTable as the isBaseCode tagging for the tokens changed which will affect the computed hashes.
        // This is a performance optimization to not suggest subsequences with baseCode for the matching.
        // Removing this optimization would not change the result as the baseCode matches are additionally checked by validating
        // that no match has a marked token (which baseCode-containing tokens are).
        hashLookupTables.remove(submission);

        List<Token> submissionTokenList = submission.getTokenList();
        for (Match match : comparison.getMatches()) {
            int start = comparison.getFirstSubmission() == submission ? match.getStartOfFirst() : match.getStartOfSecond();
            for (int offset = 0; offset < match.getLength(); offset++) {
                submissionTokenList.get(start + offset).setBasecode(true);
            }
        }
        return comparison;
    }

    private JPlagComparison swapAndCompare(Submission firstSubmission, Submission secondSubmission) {
        Submission smallerSubmission, largerSubmission;
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
     * @param firstSubmission is the submission with the smaller sequence.
     * @param secondSubmission is the submission with the larger sequence.
     * @return the comparison results.
     */
    private JPlagComparison compareInternal(Submission firstSubmission, Submission secondSubmission) {
        // first and second refer to the list of tokens of the first and second submission:
        List<Token> first = firstSubmission.getTokenList();
        List<Token> second = secondSubmission.getTokenList();

        JPlagComparison comparison = new JPlagComparison(firstSubmission, secondSubmission);
        int minimumMatchLength = options.getMinimumTokenMatch();

        // comparison uses <= because it is assumed that the last token is a pivot (FILE_END)
        if (first.size() <= minimumMatchLength || second.size() <= minimumMatchLength) {
            return comparison;
        }

        Set<Token> leftMarkedTokens = initiallyMarkedTokens(first);
        Set<Token> rightMarkedTokens = initiallyMarkedTokens(second);

        SubsequenceHashLookupTable leftLookupTable = subsequenceHashLookupTableForSubmission(firstSubmission, leftMarkedTokens);
        SubsequenceHashLookupTable rightLookupTable = subsequenceHashLookupTableForSubmission(secondSubmission, rightMarkedTokens);

        List<Match> matches = new ArrayList<>();

        int maximumMatchLength;
        do {
            maximumMatchLength = minimumMatchLength;
            matches.clear();
            for (int leftStartIndex = 0; leftStartIndex < first.size() - maximumMatchLength; leftStartIndex++) {
                int leftSubsequenceHash = leftLookupTable.subsequenceHashForStartIndex(leftStartIndex);
                if (leftMarkedTokens.contains(first.get(leftStartIndex)) || leftSubsequenceHash == SubsequenceHashLookupTable.NO_HASH) {
                    continue;
                }
                List<Integer> possiblyMatchingRightStartIndexes = rightLookupTable
                        .startIndexesOfPossiblyMatchingSubsequencesForSubsequenceHash(leftSubsequenceHash);
                for (Integer rightStartIndex : possiblyMatchingRightStartIndexes) {
                    // comparison uses >= because it is assumed that the last token is a pivot (FILE_END)
                    if (rightMarkedTokens.contains(second.get(rightStartIndex)) || maximumMatchLength >= second.size() - rightStartIndex) {
                        continue;
                    }

                    if (!subsequencesAreMatchingAndNotMarked(first.subList(leftStartIndex, leftStartIndex + maximumMatchLength), leftMarkedTokens,
                            second.subList(rightStartIndex, rightStartIndex + maximumMatchLength), rightMarkedTokens)) {
                        continue;
                    }

                    // expand match
                    int offset = maximumMatchLength;
                    while (first.get(leftStartIndex + offset).type == second.get(rightStartIndex + offset).type
                            && !leftMarkedTokens.contains(first.get(leftStartIndex + offset))
                            && !rightMarkedTokens.contains(second.get(rightStartIndex + offset))) {
                        offset++;
                    }

                    if (offset > maximumMatchLength) {
                        matches.clear();
                        maximumMatchLength = offset;
                    }
                    addMatchIfNotOverlapping(matches, leftStartIndex, rightStartIndex, offset);
                }
            }
            for (int i = matches.size() - 1; i >= 0; i--) {
                Match match = matches.get(i);
                comparison.addMatch(match);
                int x = match.getStartOfFirst();  // Beginning of/in sequence A
                int y = match.getStartOfSecond();  // Beginning of/in sequence B
                for (int j = 0; j < match.getLength(); j++) {
                    leftMarkedTokens.add(first.get(x + j));
                    rightMarkedTokens.add(second.get(y + j));
                }
            }
        } while (maximumMatchLength != minimumMatchLength);
        return comparison;
    }

    /**
     * Checks if the two provided subsequences are equal and not marked. Comparison is performed backwards based on the
     * assumption that the further tokens are away, the more likely they differ. leftTokens and rightTokens must be of equal
     * size.
     * @param leftTokens The subsequence of left tokens.
     * @param leftMarkedTokens The marked tokens of the left token list.
     * @param rightTokens The subsequence of right tokens.
     * @param rightMarkedTokens The marked tokens of the right token list.
     * @return
     */
    private boolean subsequencesAreMatchingAndNotMarked(List<Token> leftTokens, Set<Token> leftMarkedTokens, List<Token> rightTokens,
            Set<Token> rightMarkedTokens) {
        for (int offset = leftTokens.size() - 1; offset >= 0; offset--) {
            Token leftToken = leftTokens.get(offset);
            Token rightToken = rightTokens.get(offset);
            if (leftToken.type != rightToken.type || leftMarkedTokens.contains(leftToken) || rightMarkedTokens.contains(rightToken)) {
                return false;
            }
        }
        return true;
    }

    private void addMatchIfNotOverlapping(List<Match> matches, int startA, int startB, int length) {
        Match match = new Match(startA, startB, length);
        for (int i = matches.size() - 1; i >= 0; i--) { // starting at the end is better(?)
            if (matches.get(i).overlaps(match)) {
                return; // no overlaps allowed!
            }
        }
        matches.add(match);
    }

    private Set<Token> initiallyMarkedTokens(List<Token> tokens) {
        return tokens.stream().filter(token -> token.type == FILE_END || token.type == SEPARATOR_TOKEN || token.isBasecode())
                .collect(Collectors.toSet());
    }

    private SubsequenceHashLookupTable subsequenceHashLookupTableForSubmission(Submission submission, Set<Token> markedTokens) {
        if (hashLookupTables.containsKey(submission)) {
            return hashLookupTables.get(submission);
        }
        SubsequenceHashLookupTable lookupTable = new SubsequenceHashLookupTable(options.getMinimumTokenMatch(), submission.getTokenList(),
                markedTokens);
        hashLookupTables.put(submission, lookupTable);
        return lookupTable;
    }
}
