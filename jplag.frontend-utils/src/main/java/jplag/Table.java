package jplag;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link HashMap} that maps Integer keys to multiple Integer values. Note that all keys with identical
 * <code>(key % prime)</code> are mapped to the same values. Specifically, <code>prime</code> is the next prime number
 * that is larger or equal to the specified size (see {@link Table#Table(int)}).
 */
public class Table { // TODO TS: I think this might map token hashes to token indices for the token array in the structure class (rename?)
    private final Map<Integer, ArrayList<Integer>> mappedEntries;
    private final int primeNumber;

    /**
     * Creates the {@link HashMap}.
     * @param size specifies the initial size and the key mapping (see {@link Table}).
     */
    public Table(int size) {
        mappedEntries = new HashMap<>(size);
        primeNumber = nextPrimeNumber(size);
    }

    /**
     * Returns all stored numbers for a key. Note that all keys with identical <code>(key % prime)</code> are mapped to the
     * same values (see {@link Table}).
     * @param key is the specific key.
     * @return the stored numbers or null if nothing is stored.
     */
    public final List<Integer> get(int key) {
        int actualKey = key % primeNumber;
        if (mappedEntries.containsKey(actualKey)) {
            return new ArrayList<>(mappedEntries.get(actualKey));
        }
        return null;
    }

    /**
     * Stores a number for a key, does not replace the previous stored numbers for that key. Note that all keys with
     * identical <code>(key % prime)</code> are mapped to the same values (see {@link Table}).
     * @param key is the specific key.
     * @param value is the number to store.
     */
    public final void add(int key, int value) { // TODO TS: Should be renamed to put
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
