package de.jplag;

import static de.jplag.TokenConstants.FILE_END;
import static de.jplag.TokenConstants.SEPARATOR_TOKEN;

import java.util.ArrayList;
import java.util.List;

import de.jplag.options.JPlagOptions;

/**
 * This class implements the Greedy String Tiling algorithm as introduced by Michael Wise. However, it is very specific
 * to the classes {@link TokenList}, {@link Token}, and {@link Match}. While this class was reworked, it still contains
 * some quirks from the initial version.
 * @see <a href=
 * "https://www.researchgate.net/publication/262763983_String_Similarity_via_Greedy_String_Tiling_and_Running_Karp-Rabin_Matching">
 * String Similarity via Greedy String Tiling and Running Karp−Rabin Matching </a>
 */
public class GreedyStringTiling {

    private final JPlagOptions options;

    public GreedyStringTiling(JPlagOptions options) {
        this.options = options;
    }

    /**
     * Creating hashes in linear time. The hash-code will be written in every Token for the next &lt;hashLength&gt; token
     * (includes the Token itself).
     * @param tokenList contains the tokens.
     * @param hashLength is the hash length (condition: 1 &lt; hashLength &lt; 26)
     * @param makeTable determines if a simple hash table is created in the structure.
     */
    public void createHashes(TokenList tokenList, int hashLength, boolean makeTable) {
        // Here the upper boundary of the hash length is set.
        // It is determined by the number of bits of the 'int' data type and the number of tokens.
        if (hashLength < 1) {
            hashLength = 1;
        }
        hashLength = (hashLength < 26 ? hashLength : 25);

        if (tokenList.size() < hashLength) {
            return;
        }

        int modulo = ((1 << 6) - 1);   // Modulo 64!

        int loops = tokenList.size() - hashLength;
        tokenList.tokenHashes = (makeTable ? new TokenHashMap(3 * loops) : null);
        int hash = 0;
        int hashedLength = 0;
        for (int i = 0; i < hashLength; i++) {
            hash = (2 * hash) + (tokenList.getToken(i).type & modulo);
            hashedLength++;
            if (tokenList.getToken(i).isMarked()) {
                hashedLength = 0;
            }
        }
        int factor = (hashLength != 1 ? (2 << (hashLength - 2)) : 1);

        if (makeTable) {
            for (int i = 0; i < loops; i++) {
                if (hashedLength >= hashLength) {
                    tokenList.getToken(i).setHash(hash);
                    tokenList.tokenHashes.put(hash, i);   // add into hashtable
                } else {
                    tokenList.getToken(i).setHash(-1);
                }
                hash -= factor * (tokenList.getToken(i).type & modulo);
                hash = (2 * hash) + (tokenList.getToken(i + hashLength).type & modulo);
                if (tokenList.getToken(i + hashLength).isMarked()) {
                    hashedLength = 0;
                } else {
                    hashedLength++;
                }
            }
        } else {
            for (int i = 0; i < loops; i++) {
                tokenList.getToken(i).setHash((hashedLength >= hashLength) ? hash : -1);
                hash -= factor * (tokenList.getToken(i).type & modulo);
                hash = (2 * hash) + (tokenList.getToken(i + hashLength).type & modulo);
                if (tokenList.getToken(i + hashLength).isMarked()) {
                    hashedLength = 0;
                } else {
                    hashedLength++;
                }
            }
        }
        tokenList.hashLength = hashLength;
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
        // if hashtable exists in first but not in second structure: flip around!
        if (largerSubmission.getTokenList().tokenHashes == null && smallerSubmission.getTokenList().tokenHashes != null) {
            Submission swap = smallerSubmission;
            smallerSubmission = largerSubmission;
            largerSubmission = swap;
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
        TokenList first = firstSubmission.getTokenList();
        TokenList second = secondSubmission.getTokenList();

        // Initialize:
        JPlagComparison comparison = new JPlagComparison(firstSubmission, secondSubmission);
        int minimumTokenMatch = options.getMinimumTokenMatch(); // minimal required token match

        if (first.size() <= minimumTokenMatch || second.size() <= minimumTokenMatch) { // <= because of pivots!
            return comparison;
        }

        markTokens(first, isBaseCodeComparison);
        markTokens(second, isBaseCodeComparison);

        // create hashes:
        if (first.hashLength != minimumTokenMatch) {
            createHashes(first, minimumTokenMatch, isBaseCodeComparison); // don't make table if it is not a base code comparison
        }
        if (second.hashLength != minimumTokenMatch || second.tokenHashes == null) {
            createHashes(second, minimumTokenMatch, true);
        }

        List<Match> matches = new ArrayList<>();

        // start the black magic:
        int maxMatch;
        do {
            maxMatch = minimumTokenMatch;
            matches.clear();
            for (int x = 0; x < first.size() - maxMatch; x++) {
                List<Integer> hashedTokens = second.tokenHashes.get(first.getToken(x).getHash());
                if (first.getToken(x).isMarked() || first.getToken(x).getHash() == -1) {
                    continue;
                }
                inner: for (Integer y : hashedTokens) {
                    if (second.getToken(y).isMarked() || maxMatch >= second.size() - y) { // >= because of pivots!
                        continue;
                    }

                    int j, hx, hy;
                    for (j = maxMatch - 1; j >= 0; j--) { // begins comparison from behind
                        if (first.getToken(hx = x + j).type != second.getToken(hy = y + j).type || first.getToken(hx).isMarked()
                                || second.getToken(hy).isMarked()) {
                            continue inner;
                        }
                    }

                    // expand match
                    j = maxMatch;
                    while (first.getToken(hx = x + j).type == second.getToken(hy = y + j).type && !first.getToken(hx).isMarked()
                            && !second.getToken(hy).isMarked()) {
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
                    first.getToken(x).setMarked(true); // mark all Tokens!
                    second.getToken(y).setMarked(true);
                    if (isBaseCodeComparison) {
                        first.getToken(x).setBasecode(true);
                        second.getToken(y).setBasecode(true);
                    }
                    x++;
                    y++;
                }
            }

        } while (maxMatch != minimumTokenMatch);

        return comparison;
    }

    private void addMatchIfNotOverlapping(List<Match> matches, int startA, int startB, int length) {
        for (int i = matches.size() - 1; i >= 0; i--) { // starting at the end is better(?)
            if (matches.get(i).overlap(startA, startB, length)) {
                return; // no overlaps allowed!
            }
        }
        matches.add(new Match(startA, startB, length));
    }

    /**
     * Disable finding a match at separator tokens and basecode matches for non-basecode comparisons.
     * @param tokenList Tokens to mark.
     * @param isBaseCodeComparison Whether the {@link Token#basecode} matches should be enabled for matching.
     */
    private void markTokens(TokenList tokenList, boolean isBaseCodeComparison) {
        for (Token token : tokenList.allTokens()) {
            if (isBaseCodeComparison) {
                token.setMarked(token.type == FILE_END || token.type == SEPARATOR_TOKEN);
            } else {
                token.setMarked(token.type == FILE_END || token.type == SEPARATOR_TOKEN || (token.isBasecode() && options.hasBaseCode()));
            }
        }
    }
}
