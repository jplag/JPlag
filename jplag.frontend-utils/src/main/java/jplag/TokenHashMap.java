package jplag;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link HashMap} that maps Integer keys to multiple Integer values. Note that all keys with identical
 * <code>(key % prime)</code> are mapped to the same values. Specifically, <code>prime</code> is the next prime number
 * that is larger or equal to the specified size (see {@link TokenHashMap#Table(int)}).
 */
public class TokenHashMap {
    private final Map<Integer, ArrayList<Integer>> mappedEntries;
    private final int primeNumber;

    /**
     * Creates the {@link HashMap}.
     * @param size specifies the initial size and the key mapping (see {@link TokenHashMap}).
     */
    public TokenHashMap(int size) {
        mappedEntries = new HashMap<>(size);
        primeNumber = nextPrimeNumber(size);
    }

    /**
     * Returns all stored numbers for a key. Note that all keys with identical <code>(key % prime)</code> are mapped to the
     * same values (see {@link TokenHashMap}).
     * @param key is the specific key.
     * @return the stored numbers or an empty list if nothing is stored.
     */
    public final List<Integer> get(int key) {
        int actualKey = key % primeNumber;
        if (mappedEntries.containsKey(actualKey)) {
            return new ArrayList<>(mappedEntries.get(actualKey));
        }
        return Collections.emptyList();
    }

    /**
     * Stores a number for a key, does not replace the previous stored numbers for that key. Note that all keys with
     * identical <code>(key % prime)</code> are mapped to the same values (see {@link TokenHashMap}).
     * @param key is the specific key.
     * @param value is the number to store.
     */
    public final void put(int key, int value) {
        int actualKey = key % primeNumber;
        if (mappedEntries.containsKey(actualKey)) {
            mappedEntries.get(actualKey).add(value);
        } else {
            ArrayList<Integer> entries = new ArrayList<>();
            entries.add(value);
            mappedEntries.put(actualKey, entries);
        }
    }

    /**
     * Calculates the next prime number (including 1) that is larger or equal to a given number.
     * @param number is the give number.
     * @return the next prime number.
     */
    private int nextPrimeNumber(int number) {
        if (number <= 1) {
            return 1;
        }
        for (int possiblePrime = number; possiblePrime < 2 * number; possiblePrime++) { // Bertrand's postulate
            BigInteger bigInt = BigInteger.valueOf(possiblePrime);
            if (bigInt.isProbablePrime(100)) {
                return possiblePrime;
            }
        }
        throw new IllegalStateException("Should never be reached because of Bertrand's postulate!");
    }
}
