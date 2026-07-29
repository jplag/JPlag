package de.jplag.reporting.reportobject.model;

/**
 * Represents a matched code segment between two files. Stores metadata about the matching code positions in both files.
 * @param firstFileName Name of the first file involved in the match
 * @param secondFileName Name of the second file involved in the match
 * @param startInFirst Start position of the match in the first file
 * @param endInFirst End position of the match in the first file
 * @param startInSecond Start position of the match in the second file
 * @param endInSecond End position of the match in the second file
 * @param lengthOfFirst Length of the matched segment in the first file
 * @param lengthOfSecond Length of the matched segment in the second file
 */
public record Match(String firstFileName, String secondFileName, CodePosition startInFirst, CodePosition endInFirst, CodePosition startInSecond,
        CodePosition endInSecond, int lengthOfFirst, int lengthOfSecond) {
}
