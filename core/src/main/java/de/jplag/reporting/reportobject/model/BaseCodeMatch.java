package de.jplag.reporting.reportobject.model;

/**
 * Represents a match between the base code and a submission.
 * @param fileName the file where the match occurs
 * @param start the start position of the match
 * @param end the end position of the match
 * @param tokens the number of matching tokens
 */
public record BaseCodeMatch(String fileName, CodePosition start, CodePosition end, int tokens) {
}
