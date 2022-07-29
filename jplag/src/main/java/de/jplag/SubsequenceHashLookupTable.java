package de.jplag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class to generate and store hashes over a fixed length subsequence of a given list of tokens. Hash generation is
 * optimized to work in O(n).
 */
public class SubsequenceHashLookupTable {
    private static final int MAX_HASH_LENGTH = 25;
    private static final int TOKEN_HASH_MODULO = 63;

    /** Indicator that the subsequence should not be considered for comparison matching */
    public static final int NO_HASH = -1;

    private int windowSize;
    private List<Token> tokens;
    private int[] subsequenceHashes;
    private Map<Integer, List<Integer>> startIndexToSubsequenceHashesMap;

    /**
     * Generates a new subsequence hash lookup table. Performance is optimized to compute hashes in O(n).
     * @param windowSize the size of the subsequences.
     * @param tokens the tokens to hash over.
     * @param markedTokens the set of marked tokens.
     */
    public SubsequenceHashLookupTable(int windowSize, List<Token> tokens, Set<Token> markedTokens) {
        windowSize = Math.max(1, windowSize);
        windowSize = Math.min(MAX_HASH_LENGTH, windowSize);
        this.windowSize = windowSize;
        this.tokens = tokens;

        if (tokens.size() < windowSize) {
            return;
        }

        subsequenceHashes = new int[tokens.size() - windowSize];
        startIndexToSubsequenceHashesMap = new HashMap<>(subsequenceHashes.length);
        computeSubsequenceHashes(markedTokens);
    }

    /** Returns the size of the subsequences used for hashing */
    public int getWindowSize() {
        return windowSize;
    }

    /** Returns the list of tokens for which the hashes were computed */
    public List<Token> getTokens() {
        return tokens;
    }

    /**
     * Returns the hash over the subsequence from startIndex to startIndex+windowSize.
     * @param startIndex the start index.
     * @return the hash of the requested subsequence.
     */
    public int subsequenceHashForStartIndex(int startIndex) {
        return subsequenceHashes[startIndex];
    }

    /**
     * Returns a list of all start indexes of possible subsequences for the given subsequence hash.
     * @param subsequenceHash the hash value to obtain possibly matching subsequence start indexes for.
     * @return a list with possible matching start indexes.
     */
    public List<Integer> startIndexesOfPossiblyMatchingSubsequencesForSubsequenceHash(int subsequenceHash) {
        if (startIndexToSubsequenceHashesMap.containsKey(subsequenceHash)) {
            return startIndexToSubsequenceHashesMap.get(subsequenceHash);
        }
        return List.of();
    }

    /**
     * Creates hashes for all subsequences with windowSize. Code is optimized to perform in O(n) using a windowing approach.
     * Hashes are computed by \sum from (i=0 to windowSize) with hash(tokens[offset+i]) * pow(2, hashLength-1-i)
     * @param markedTokens contains the marked tokens. Subsequences containing a marked token will receive the NO_HASH
     * value.
     */
    private void computeSubsequenceHashes(Set<Token> markedTokens) {
        int hash = 0;
        int hashedLength = 0;
        int factor = (windowSize != 1 ? (2 << (windowSize - 2)) : 1);

        for (int windowEndIndex = 0; windowEndIndex < tokens.size(); windowEndIndex++) {
            int windowStartIndex = windowEndIndex - windowSize;
            if (windowStartIndex >= 0) {
                if (hashedLength >= windowSize) {
                    subsequenceHashes[windowStartIndex] = hash;
                    addToStartIndexesToHashesMap(windowStartIndex, hash);
                } else {
                    subsequenceHashes[windowStartIndex] = NO_HASH;
                }
                hash -= factor * (hashValueForToken(tokens.get(windowStartIndex)));
            }
            hash = (2 * hash) + (hashValueForToken(tokens.get(windowEndIndex)));
            if (markedTokens.contains(tokens.get(windowEndIndex))) {
                hashedLength = 0;
            } else {
                hashedLength++;
            }
        }
    }

    private int hashValueForToken(Token token) {
        return token.type % TOKEN_HASH_MODULO;
    }

    private void addToStartIndexesToHashesMap(int startIndex, int subsequenceHash) {
        if (startIndexToSubsequenceHashesMap.containsKey(subsequenceHash)) {
            startIndexToSubsequenceHashesMap.get(subsequenceHash).add(startIndex);
        } else {
            List<Integer> startIndexes = new ArrayList<>();
            startIndexes.add(startIndex);
            startIndexToSubsequenceHashesMap.put(subsequenceHash, startIndexes);
        }
    }
}
