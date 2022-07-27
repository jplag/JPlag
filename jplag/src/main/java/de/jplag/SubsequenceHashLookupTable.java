package de.jplag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubsequenceHashLookupTable {
    private static final int MAX_HASH_LENGTH = 25;
    private static final int TOKEN_HASH_MODULO = 63;

    public static final int NO_HASH = -1;

    private int windowSize;
    private List<Token> tokens;
    private int[] subsequenceHashes;
    private Map<Integer, List<Integer>> hashTable;

    public SubsequenceHashLookupTable(int windowSize, List<Token> tokens, Set<Token> markedTokens) {
        windowSize = Math.max(1, windowSize);
        windowSize = Math.min(MAX_HASH_LENGTH, windowSize);
        this.windowSize = windowSize;
        this.tokens = tokens;

        if (tokens.size() < windowSize) {
            return;
        }

        subsequenceHashes = new int[tokens.size() - windowSize];
        hashTable = new HashMap<>(subsequenceHashes.length);
        computeSubsequenceHashes(markedTokens);
    }

    public int getWindowSize() {
        return windowSize;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public int subsequenceHashForStartIndex(int startIndex) {
        return subsequenceHashes[startIndex];
    }

    public List<Integer> startIndexesOfPossiblyMatchingSubsequencesForSubsequenceHash(int subsequenceHash) {
        if (hashTable.containsKey(subsequenceHash)) {
            return hashTable.get(subsequenceHash);
        }
        return List.of();
    }

    /**
     * Creating hashes in linear time. The hash-code will be written in every Token for the next &lt;hashLength&gt; token
     * (includes the Token itself).
     * @param tokenList contains the tokens.
     * @param markedTokens contains the marked tokens.
     * @param hashLength is the hash length (condition: 1 &lt; hashLength &lt; 26)
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
                    addToHashTable(windowStartIndex, hash);
                } else {
                    subsequenceHashes[windowStartIndex] = NO_HASH;
                }
                //windowing to create hashes in linear time
                // hash = sum over (j=0 to hashLength) with pow(2, hashLength-j-1) * hash(tokens[i+j])
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

    private void addToHashTable(int startIndex, int subsequenceHash) {
        if (hashTable.containsKey(subsequenceHash)) {
            hashTable.get(subsequenceHash).add(startIndex);
        }
        else {
            List<Integer> startIndexes = new  ArrayList<>();
            startIndexes.add(startIndex);
            hashTable.put(subsequenceHash, startIndexes);
        }
    }
}
