package de.jplag.commentextraction;

public record EnvironmentDelimiter(String begin, String end) {
    public EnvironmentDelimiter(String beginAndEnd) {
        this(beginAndEnd, beginAndEnd);
    }
}
