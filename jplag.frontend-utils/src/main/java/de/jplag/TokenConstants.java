package de.jplag;

/**
 * Super interface for all language parsers. Provides the only two token that are required from every parser.
 */
public interface TokenConstants {
    /**
     * Marks the end of the file, has a special purpose in the comparison algorithm.
     */
    final static int FILE_END = 0;

    /**
     * Used to optionally separate methods from each other with an always marked token
     */
    final static int SEPARATOR_TOKEN = 1;
}
