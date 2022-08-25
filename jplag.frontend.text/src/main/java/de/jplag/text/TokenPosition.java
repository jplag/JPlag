package de.jplag.text;

public record TokenPosition(
    int line,
    int column,
    int length
) {
}
