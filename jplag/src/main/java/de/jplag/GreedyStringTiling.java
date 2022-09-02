package de.jplag;

import static de.jplag.TokenConstants.FILE_END;
import static de.jplag.TokenConstants.SEPARATOR_TOKEN;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.jplag.options.JPlagOptions;

/**
 * This class implements the Greedy String Tiling algorithm as introduced by Michael Wise. However, it is very specific
 * to the classes {@link Token}, and {@link Match}.
 * @see <a href=
 * "https://www.researchgate.net/publication/262763983_String_Similarity_via_Greedy_String_Tiling_and_Running_Karp-Rabin_Matching">
 * String Similarity via Greedy String Tiling and Running Karp−Rabin Matching </a>
 */
public class GreedyStringTiling {

    private final int minimumMatchLength;
    private Map<Submission, SubsequenceHashLookupTable> hashLookupTables = new IdentityHashMap<>();
    private Map<Submission, Set<Token>> baseCodeMarkings = new IdentityHashMap<>();

    public GreedyStringTiling(JPlagOptions options) {
        this.minimumMatchLength = options.minimumTokenMatch();
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
        Set<Token> baseCodeMarking = new HashSet<>();
        for (Match match : comparison.matches()) {
            int startIndex = comparison.firstSubmission() == submission ? match.startOfFirst() : match.startOfSecond();
            baseCodeMarking.addAll(submissionTokenList.subList(startIndex, startIndex + match.length()));
        }
        baseCodeMarkings.put(submission, baseCodeMarking);

        return comparison;
    }

    private JPlagComparison swapAndCompare(Submission firstSubmission, Submission secondSubmission) {
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

        // comparison uses <= because it is assumed that the last token is a pivot (FILE_END)
        if (leftTokens.size() <= minimumMatchLength || rightTokens.size() <= minimumMatchLength) {
            return new JPlagComparison(leftSubmission, rightSubmission, List.of());
        }

        Set<Token> leftMarkedTokens = initiallyMarkedTokens(leftSubmission);
        Set<Token> rightMarkedTokens = initiallyMarkedTokens(rightSubmission);

        SubsequenceHashLookupTable leftLookupTable = subsequenceHashLookupTableForSubmission(leftSubmission, leftMarkedTokens);
        SubsequenceHashLookupTable rightLookupTable = subsequenceHashLookupTableForSubmission(rightSubmission, rightMarkedTokens);

        int maximumMatchLength;
        List<Match> globalMatches = new ArrayList<>();
        do {
            maximumMatchLength = minimumMatchLength;
            List<Match> iterationMatches = new ArrayList<>();
            for (int leftStartIndex = 0; leftStartIndex < leftTokens.size() - maximumMatchLength; leftStartIndex++) {
                int leftSubsequenceHash = leftLookupTable.subsequenceHashForStartIndex(leftStartIndex);
                if (leftMarkedTokens.contains(leftTokens.get(leftStartIndex)) || leftSubsequenceHash == SubsequenceHashLookupTable.NO_HASH) {
                    continue;
                }
                List<Integer> possiblyMatchingRightStartIndexes = rightLookupTable
                        .startIndexesOfPossiblyMatchingSubsequencesForSubsequenceHash(leftSubsequenceHash);
                for (Integer rightStartIndex : possiblyMatchingRightStartIndexes) {
                    // comparison uses >= because it is assumed that the last token is a pivot (FILE_END)
                    if (rightMarkedTokens.contains(rightTokens.get(rightStartIndex)) || maximumMatchLength >= rightTokens.size() - rightStartIndex) {
                        continue;
                    }

                    if (!subsequencesAreMatchingAndNotMarked(leftTokens.subList(leftStartIndex, leftStartIndex + maximumMatchLength),
                            leftMarkedTokens, rightTokens.subList(rightStartIndex, rightStartIndex + maximumMatchLength), rightMarkedTokens)) {
                        continue;
                    }

                    // expand match
                    int offset = maximumMatchLength;
                    while (leftTokens.get(leftStartIndex + offset).type == rightTokens.get(rightStartIndex + offset).type
                            && !leftMarkedTokens.contains(leftTokens.get(leftStartIndex + offset))
                            && !rightMarkedTokens.contains(rightTokens.get(rightStartIndex + offset))) {
                        offset++;
                    }

                    if (offset > maximumMatchLength) {
                        iterationMatches.clear();
                        maximumMatchLength = offset;
                    }
                    Match match = new Match(leftStartIndex, rightStartIndex, offset);
                    addMatchIfNotOverlapping(iterationMatches, match);
                }
            }
            for (Match match : iterationMatches) {
                addMatchIfNotOverlapping(globalMatches, match);
                int leftStartIndex = match.startOfFirst();
                int rightStartIndex = match.startOfSecond();
                for (int offset = 0; offset < match.length(); offset++) {
                    leftMarkedTokens.add(leftTokens.get(leftStartIndex + offset));
                    rightMarkedTokens.add(rightTokens.get(rightStartIndex + offset));
                }
            }
        } while (maximumMatchLength != minimumMatchLength);
        return new JPlagComparison(leftSubmission, rightSubmission, globalMatches);
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

    private void addMatchIfNotOverlapping(List<Match> matches, Match match) {
        for (int i = matches.size() - 1; i >= 0; i--) { // starting at the end is better(?)
            if (matches.get(i).overlaps(match)) {
                return; // no overlaps allowed!
            }
        }
        matches.add(match);
    }

    private Set<Token> initiallyMarkedTokens(Submission submission) {
        Set<Token> baseCodeTokens = baseCodeMarkings.get(submission);
        return submission.getTokenList().stream().filter(
                token -> token.type == FILE_END || token.type == SEPARATOR_TOKEN || (baseCodeTokens != null && baseCodeTokens.contains(token)))
                .collect(Collectors.toSet());
    }

    private SubsequenceHashLookupTable subsequenceHashLookupTableForSubmission(Submission submission, Set<Token> markedTokens) {
        if (hashLookupTables.containsKey(submission)) {
            return hashLookupTables.get(submission);
        }
        SubsequenceHashLookupTable lookupTable = new SubsequenceHashLookupTable(minimumMatchLength, submission.getTokenList(), markedTokens);
        hashLookupTables.put(submission, lookupTable);
        return lookupTable;
    }
}
