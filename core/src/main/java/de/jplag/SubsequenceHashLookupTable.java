package de.jplag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class to generate and store hashes over a fixed length subsequence of a given list of values. Hash generation is
 * optimized to work in O(n).
 */
class SubsequenceHashLookupTable {
    /**
     * Value combination is chosen such that the maximum possible hash value does not exceed Int.max. Computation formula
     * for maximum hash value is \sum from (i=0 to MAX_HASH_LENGTH - 1) with (HASH_MODULO - 1) * 2^i
     */
    private static final int MAX_HASH_LENGTH = 25;
    private static final int HASH_MODULO = 64;

    /** Indicator that the subsequence should not be considered for comparison matching */
    public static final int NO_HASH = -1;

    private final int windowSize;
    private final int[] values;
    private int[] subsequenceHashes;
    private Map<Integer, List<Integer>> startIndexToSubsequenceHashesMap;

    /**
     * Generates a new subsequence hash lookup table. Performance is optimized to compute hashes in O(n).
     * @param windowSize the size of the subsequences.
     * @param values the values to hash over.
     * @param markedIndexes the indexes of marked values. Subsequences containing a marked value obtain the {@link #NO_HASH}
     * value.
     */
    SubsequenceHashLookupTable(int windowSize, int[] values, Set<Integer> markedIndexes) {
        windowSize = Math.max(1, windowSize);
        windowSize = Math.min(MAX_HASH_LENGTH, windowSize);
        this.windowSize = windowSize;
        this.values = values;

        if (values.length < windowSize) {
            return;
        }

        subsequenceHashes = new int[values.length - windowSize];
        startIndexToSubsequenceHashesMap = new HashMap<>(subsequenceHashes.length);
        computeSubsequenceHashes(markedIndexes);
    }

    /** Returns the size of the subsequences used for hashing */
    int getWindowSize() {
        return windowSize;
    }

    /** Returns the list of values for which the hashes were computed */
    int[] getValues() {
        return values;
    }

    /**
     * Returns the hash over the subsequence from startIndex to startIndex+windowSize.
     * @param startIndex the start index.
     * @return the hash of the requested subsequence.
     */
    int subsequenceHashForStartIndex(int startIndex) {
        return subsequenceHashes[startIndex];
    }

    /**
     * Returns a list of all start indexes of possible subsequences for the given subsequence hash.
     * @param subsequenceHash the hash value to obtain possibly matching subsequence start indexes for.
     * @return a list with possible matching start indexes.
     */
    List<Integer> startIndexesOfPossiblyMatchingSubsequencesForSubsequenceHash(int subsequenceHash) {
        if (startIndexToSubsequenceHashesMap.containsKey(subsequenceHash)) {
            return startIndexToSubsequenceHashesMap.get(subsequenceHash);
        }
        return List.of();
    }

    /**
     * Creates hashes for all subsequences with windowSize. Code is optimized to perform in O(n) using a windowing approach.
     * Hashes are computed by \sum from (i=0 to windowSize) with hash(values[offset+i]) * 2^(hashLength-1-i)
     * @param markedIndexes contains the indexes of marked values. Subsequences containing a marked value will receive the
     * {@link #NO_HASH} value.
     */
    private void computeSubsequenceHashes(Set<Integer> markedIndexes) {
        int hash = 0;
        int hashedLength = 0;
        int factor = (windowSize != 1 ? (2 << (windowSize - 2)) : 1);

        for (int windowEndIndex = 0; windowEndIndex < values.length; windowEndIndex++) {
            int windowStartIndex = windowEndIndex - windowSize;
            if (windowStartIndex >= 0) {
                if (hashedLength >= windowSize) {
                    subsequenceHashes[windowStartIndex] = hash;
                    addToStartIndexesToHashesMap(windowStartIndex, hash);
                } else {
                    subsequenceHashes[windowStartIndex] = NO_HASH;
                }
                hash -= factor * hashValueForValue(values[windowStartIndex]);
            }
            hash = (2 * hash) + hashValueForValue(values[windowEndIndex]);
            if (markedIndexes.contains(windowEndIndex)) {
                hashedLength = 0;
            } else {
                hashedLength++;
            }
        }
    }

    private int hashValueForValue(int value) {
        return value % HASH_MODULO;
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
