package de.jplag.commentextraction;

/**
 * Delimiters signaling the start and end of an environment.
 * @param begin Starting delimiter
 * @param end Ending delimiter
 */
public record EnvironmentDelimiter(String begin, String end) {
    /**
     * Environment with identical delimiters for start and end.
     * @param beginAndEnd Delimiter signaling the start and end of the environment.
     */
    public EnvironmentDelimiter(String beginAndEnd) {
        this(beginAndEnd, beginAndEnd);
    }
}
