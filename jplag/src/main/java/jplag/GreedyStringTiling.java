package jplag;

import java.util.List;

/**
 * This class implements the Greedy String Tiling algorithm as introduced by Michael Wise. However, it is very specific
 * to the classes {@link TokenList}, {@link Token}, as well as {@link Matches} and {@link Match}. While is class was
 * reworked, it still contains some quirks from the initial version.
 * @see <a href=
 * "https://www.researchgate.net/publication/262763983_String_Similarity_via_Greedy_String_Tiling_and_Running_Karp-Rabin_Matching">
 * String Similarity via Greedy String Tiling and Running Karp−Rabin Matching </a>
 */
public class GreedyStringTiling implements TokenConstants {

    private Matches matches = new Matches();
    private JPlag program;

    public GreedyStringTiling(JPlag program) {
        this.program = program;
    }

    /**
     * Creating hashes in linear time. The hash-code will be written in every Token for the next <hash_length> token
     * (includes the Token itself).
     * @param tokenList contains the tokens.
     * @param hashLength is the hash length (condition: 1 < hashLength < 26)
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
            if (tokenList.getToken(i).marked) {
                hashedLength = 0;
            }
        }
        int factor = (hashLength != 1 ? (2 << (hashLength - 2)) : 1);

        if (makeTable) {
            for (int i = 0; i < loops; i++) {
                if (hashedLength >= hashLength) {
                    tokenList.getToken(i).hash = hash;
                    tokenList.tokenHashes.put(hash, i);   // add into hashtable
                } else {
                    tokenList.getToken(i).hash = -1;
                }
                hash -= factor * (tokenList.getToken(i).type & modulo);
                hash = (2 * hash) + (tokenList.getToken(i + hashLength).type & modulo);
                if (tokenList.getToken(i + hashLength).marked) {
                    hashedLength = 0;
                } else {
                    hashedLength++;
                }
            }
        } else {
            for (int i = 0; i < loops; i++) {
                tokenList.getToken(i).hash = (hashedLength >= hashLength) ? hash : -1;
                hash -= factor * (tokenList.getToken(i).type & modulo);
                hash = (2 * hash) + (tokenList.getToken(i + hashLength).type & modulo);
                if (tokenList.getToken(i + hashLength).marked) {
                    hashedLength = 0;
                } else {
                    hashedLength++;
                }
            }
        }
        tokenList.hash_length = hashLength;
    }

    public final JPlagComparison compare(Submission firstSubmission, Submission secondSubmission) {
        return compare(firstSubmission, secondSubmission, false);
    }

    public final JPlagComparison compareWithBaseCode(Submission firstSubmission, Submission secondSubmission) {
        return compare(firstSubmission, secondSubmission, true);
    }

    private final JPlagComparison compare(Submission firstSubmission, Submission secondSubmission, boolean withBaseCode) {
        Submission smallerSubmission, largerSubmission;
        if (firstSubmission.tokenList.size() > secondSubmission.tokenList.size()) {
            smallerSubmission = secondSubmission;
            largerSubmission = firstSubmission;
        } else {
            smallerSubmission = firstSubmission;
            largerSubmission = secondSubmission;
        }
        // if hashtable exists in first but not in second structure: flip around!
        if (largerSubmission.tokenList.tokenHashes == null && smallerSubmission.tokenList.tokenHashes != null) {
            Submission swap = smallerSubmission;
            smallerSubmission = largerSubmission;
            largerSubmission = swap;
        }
        int minTokenMatch = program.getOptions().getMinTokenMatch();

        return compare(smallerSubmission, largerSubmission, minTokenMatch, withBaseCode);
    }

    /**
     * Compares two submissions.
     * @param firstSubmission is the submission with the smaller sequence.
     * @param secondSubmission is the submission with the larger sequence.
     * @param minimalTokenMatch is the minimal required token match.
     * @param withBaseCode specifies whether one of the submissions is the base code.
     * @return the comparison results.
     */
    private final JPlagComparison compare(Submission firstSubmission, Submission secondSubmission, int minimalTokenMatch, boolean withBaseCode) {
        // first and second refer to the list of tokens of the first and second submission:
        TokenList first = firstSubmission.tokenList;
        TokenList second = secondSubmission.tokenList;

        // FILE_END used as pivot

        // Initialize:
        JPlagComparison comparison = withBaseCode ? new JPlagBaseCodeComparison(firstSubmission, secondSubmission)
                : new JPlagComparison(firstSubmission, secondSubmission);

        if (first.size() <= minimalTokenMatch || second.size() <= minimalTokenMatch) { // <= because of pivots!
            return comparison;
        }

        markTokens(first, withBaseCode);
        markTokens(second, withBaseCode);

        // create hashes:
        if (first.hash_length != minimalTokenMatch) {
            createHashes(first, minimalTokenMatch, withBaseCode); // don't make table if it is not a base code comparison
        }
        if (second.hash_length != minimalTokenMatch || second.tokenHashes == null) {
            createHashes(second, minimalTokenMatch, true);
        }

        // start the black magic:
        int maxmatch;
        do {
            maxmatch = minimalTokenMatch;
            matches.clear();
            for (int x = 0; x < first.size() - maxmatch; x++) {
                List<Integer> hashedTokens = second.tokenHashes.get(first.getToken(x).hash);
                if (first.getToken(x).marked || first.getToken(x).hash == -1) {
                    continue;
                }
                inner: for (Integer y : hashedTokens) {
                    if (second.getToken(y).marked || maxmatch >= second.size() - y) { // >= because of pivots!
                        continue;
                    }

                    int j, hx, hy;
                    for (j = maxmatch - 1; j >= 0; j--) { // begins comparison from behind
                        if (first.getToken(hx = x + j).type != second.getToken(hy = y + j).type || first.getToken(hx).marked
                                || second.getToken(hy).marked) {
                            continue inner;
                        }
                    }

                    // expand match
                    j = maxmatch;
                    while (first.getToken(hx = x + j).type == second.getToken(hy = y + j).type && !first.getToken(hx).marked
                            && !second.getToken(hy).marked) {
                        j++;
                    }

                    if (j > maxmatch && !withBaseCode || j != maxmatch && withBaseCode) {  // new biggest match? -> delete current smaller
                        matches.clear();
                        maxmatch = j;
                    }
                    matches.addMatch(x, y, j);  // add match
                }
            }
            for (int i = matches.size() - 1; i >= 0; i--) {
                int x = matches.matches[i].startA;  // Beginning of/in sequence A
                int y = matches.matches[i].startB;  // Beginning of/in sequence B
                comparison.addMatch(x, y, matches.matches[i].length);
                // in order that "Match" will be newly build (because reusing)
                for (int j = matches.matches[i].length; j > 0; j--) {
                    first.getToken(x).marked = second.getToken(y).marked = true; // mark all Tokens!
                    if (withBaseCode) {
                        first.getToken(x).basecode = second.getToken(y).basecode = true;
                    }
                    x++;
                    y++;
                }
            }

        } while (maxmatch != minimalTokenMatch);

        return comparison;
    }

    private void markTokens(TokenList tokenList, boolean withBaseCode) {
        for (Token token : tokenList.allTokens()) {
            if (withBaseCode) {
                token.marked = token.type == FILE_END || token.type == SEPARATOR_TOKEN;
            } else {
                token.marked = token.type == FILE_END || token.type == SEPARATOR_TOKEN || (token.basecode && program.getOptions().hasBaseCode());
            }
        }
    }

    public void resetBaseSubmission(Submission submission) {
        for (Token token : submission.tokenList.allTokens()) {
            token.basecode = false;
        }
    }
}
