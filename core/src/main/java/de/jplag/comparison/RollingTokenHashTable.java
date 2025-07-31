package de.jplag.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A subsequence hash look-up table to generate and store rolling hashes over a fixed length subsequence of a given
 * sequence of token values. Hash generation is optimized to work in O(n).
 */
class RollingTokenHashTable {
    /**
     * Value combination is chosen such that the maximum possible hash value does not exceed Int.max. Computation formula
     * for maximum hash value is \sum from (i=0 to MAX_HASH_LENGTH - 1) with (HASH_MODULO - 1) * 2^i.
     */
    private static final int MAX_HASH_LENGTH = 25;
    private static final int HASH_MODULO = 64;

    /** Indicator that the subsequence should not be considered for comparison matching. */
    public static final int NO_HASH = -1;

    private final int windowSize;
    private final int[] tokenSequence;
    private int[] subsequenceHashes;
    private Map<Integer, List<Integer>> startIndexToSubsequenceHashesMap;

    /**
     * Generates a new subsequence hash lookup table. Performance is optimized to compute hashes in O(n).
     * @param windowSize the size of the rolling subsequence (corresponds to the minimum token match value).
     * @param tokenSequence the values to hash over, which is an integer-based token sequence.
     * @param markedTokens denotes which values (meaning which tokens) are marked. Subsequences containing a marked value
     * obtain the {@link #NO_HASH} value, thus preventing them from being matched.
     */
    RollingTokenHashTable(int windowSize, int[] tokenSequence, boolean[] markedTokens) {
        this.windowSize = Math.clamp(windowSize, 1, MAX_HASH_LENGTH);
        this.tokenSequence = tokenSequence;

        if (tokenSequence.length < this.windowSize) {
            return;
        }

        subsequenceHashes = new int[tokenSequence.length - this.windowSize + 1];
        startIndexToSubsequenceHashesMap = HashMap.newHashMap(subsequenceHashes.length);
        computeSubsequenceHashes(markedTokens);
    }

    /**
     * Returns the hash over the subsequence from startIndex to startIndex + windowSize.
     * @param startIndex the start index.
     * @return the hash of the requested subsequence.
     */
    int getHashAt(int startIndex) {
        return subsequenceHashes[startIndex];
    }

    /**
     * Returns a list of all start indexes of possibly matching subsequences for the given subsequence hash.
     * @param subsequenceHash the hash value to obtain possibly matching subsequence start indexes for.
     * @return a list with possible matching start indexes.
     */
    List<Integer> getStartIndicesForHash(int subsequenceHash) {
        return startIndexToSubsequenceHashesMap.getOrDefault(subsequenceHash, List.of());
    }

    /**
     * Creates hashes for all subsequences with windowSize. Code is optimized to perform in O(n) using a windowing approach.
     * Hashes are computed by \sum from (i=0 to windowSize) with hash(values[offset+i]) * 2^(hashLength-1-i).
     * @param marked contains which of the values are marked. Subsequences containing a marked value will receive the
     * {@link #NO_HASH} value.
     */
    private void computeSubsequenceHashes(boolean[] marked) {
        int hash = 0;
        int hashedLength = 0;

        for (int windowEndIndex = 0; windowEndIndex < tokenSequence.length; windowEndIndex++) {
            int windowStartIndex = windowEndIndex - windowSize;
            if (windowStartIndex >= 0) {
                if (hashedLength >= windowSize) {
                    subsequenceHashes[windowStartIndex] = hash;
                    startIndexToSubsequenceHashesMap.computeIfAbsent(hash, key -> new ArrayList<>()).add(windowStartIndex);
                } else {
                    subsequenceHashes[windowStartIndex] = NO_HASH;
                }
                hash -= hashToken(tokenSequence[windowStartIndex]) << (windowSize - 1); // is [...] * 2^(windowSize - 1)
            }
            hash = (hash << 1) + hashToken(tokenSequence[windowEndIndex]); // is 2 * hash + [...]
            if (marked[windowEndIndex]) {
                hashedLength = 0;
            } else {
                hashedLength++;
            }
        }
    }

    private int hashToken(int value) {
        return value % HASH_MODULO;
    }
}
