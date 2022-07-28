package de.jplag;

import static de.jplag.TokenConstants.FILE_END;
import static de.jplag.TokenConstants.SEPARATOR_TOKEN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * Preprocesses the given base code submission. Should be called before computing comparisons if there is a base code
     * submission.
     * @param baseSubmission is the base code submission. Must not be null.
     */
    public void preprocessBaseCodeSubmission(Submission baseSubmission) {
        subsequenceHashLookupTableForSubmission(baseSubmission, Set.of());
    }

    public final JPlagComparison compare(Submission firstSubmission, Submission secondSubmission) {
        return swapAndCompare(firstSubmission, secondSubmission, false);
    }

    public final JPlagComparison compareWithBaseCode(Submission firstSubmission, Submission secondSubmission) {
        return swapAndCompare(firstSubmission, secondSubmission, true);
    }

    private JPlagComparison swapAndCompare(Submission firstSubmission, Submission secondSubmission, boolean isBaseCodeComparison) {
        Submission smallerSubmission, largerSubmission;
        if (firstSubmission.getTokenList().size() > secondSubmission.getTokenList().size()) {
            smallerSubmission = secondSubmission;
            largerSubmission = firstSubmission;
        } else {
            smallerSubmission = firstSubmission;
            largerSubmission = secondSubmission;
        }
        return compare(smallerSubmission, largerSubmission, isBaseCodeComparison);
    }

    /**
     * Compares two submissions. FILE_END is used as pivot
     * @param firstSubmission is the submission with the smaller sequence.
     * @param secondSubmission is the submission with the larger sequence.
     * @param isBaseCodeComparison specifies whether one of the submissions is the base code.
     * @return the comparison results.
     */
    private JPlagComparison compare(Submission firstSubmission, Submission secondSubmission, boolean isBaseCodeComparison) {
        // first and second refer to the list of tokens of the first and second submission:
        List<Token> first = firstSubmission.getTokenList();
        List<Token> second = secondSubmission.getTokenList();

        // Initialize:
        JPlagComparison comparison = new JPlagComparison(firstSubmission, secondSubmission);
        int minimumTokenMatch = options.getMinimumTokenMatch(); // minimal required token match

        if (first.size() <= minimumTokenMatch || second.size() <= minimumTokenMatch) { // <= because of pivots!
            return comparison;
        }

        Set<Token> leftMarkedTokens = initiallyMarkedTokens(first, isBaseCodeComparison);
        Set<Token> rightMarkedTokens = initiallyMarkedTokens(second, isBaseCodeComparison);

        SubsequenceHashLookupTable leftLookupTable = subsequenceHashLookupTableForSubmission(firstSubmission, leftMarkedTokens);
        SubsequenceHashLookupTable rightLookupTable = subsequenceHashLookupTableForSubmission(secondSubmission, rightMarkedTokens);

        List<Match> matches = new ArrayList<>();

        // start the black magic:
        int maxMatch;
        do {
            maxMatch = minimumTokenMatch;
            matches.clear();
            for (int x = 0; x < first.size() - maxMatch; x++) {
                int leftSubsequenceHash = leftLookupTable.subsequenceHashForStartIndex(x);
                if (leftMarkedTokens.contains(first.get(x)) || leftSubsequenceHash == SubsequenceHashLookupTable.NO_HASH) {
                    continue;
                }
                List<Integer> hashedTokens = rightLookupTable.startIndexesOfPossiblyMatchingSubsequencesForSubsequenceHash(leftSubsequenceHash);
                for (Integer y : hashedTokens) {
                    if (rightMarkedTokens.contains(second.get(y)) || maxMatch >= second.size() - y) { // >= because of pivots!
                        continue;
                    }

                    if (!subsequencesAreMatchingAndNotMarked(first.subList(x, x + maxMatch), leftMarkedTokens, second.subList(y, y + maxMatch),
                            rightMarkedTokens)) {
                        continue;
                    }

                    // expand match
                    int j = maxMatch;
                    int hx, hy;
                    while (first.get(hx = x + j).type == second.get(hy = y + j).type && !leftMarkedTokens.contains(first.get(hx))
                            && !rightMarkedTokens.contains(second.get(hy))) {
                        j++;
                    }

                    if (j > maxMatch && !isBaseCodeComparison || j != maxMatch && isBaseCodeComparison) {  // new biggest match? -> delete current
                                                                                                           // smaller
                        matches.clear();
                        maxMatch = j;
                    }
                    addMatchIfNotOverlapping(matches, x, y, j);
                }
            }
            for (int i = matches.size() - 1; i >= 0; i--) {
                int x = matches.get(i).getStartOfFirst();  // Beginning of/in sequence A
                int y = matches.get(i).getStartOfSecond();  // Beginning of/in sequence B
                comparison.addMatch(x, y, matches.get(i).getLength());
                // in order that "Match" will be newly build (because reusing)
                for (int j = matches.get(i).getLength(); j > 0; j--) {
                    leftMarkedTokens.add(first.get(x));
                    rightMarkedTokens.add(second.get(y));
                    if (isBaseCodeComparison) {
                        first.get(x).setBasecode(true);
                        second.get(y).setBasecode(true);
                    }
                    x++;
                    y++;
                }
            }

        } while (maxMatch != minimumTokenMatch);

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
        for (int i = matches.size() - 1; i >= 0; i--) { // starting at the end is better(?)
            if (matches.get(i).overlap(startA, startB, length)) {
                return; // no overlaps allowed!
            }
        }
        matches.add(new Match(startA, startB, length));
    }

    private Set<Token> initiallyMarkedTokens(List<Token> tokens, boolean isBaseCodeComparison) {
        Set<Token> markedTokens = new HashSet<Token>();
        for (Token token : tokens) {
            if (token.type == FILE_END || token.type == SEPARATOR_TOKEN || (!isBaseCodeComparison && token.isBasecode() && options.hasBaseCode())) {
                markedTokens.add(token);
            }
        }
        return markedTokens;
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
