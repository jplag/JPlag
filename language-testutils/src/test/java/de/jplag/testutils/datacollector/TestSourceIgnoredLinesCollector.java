package de.jplag.testutils.datacollector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Collects relevant source code lines by filtering out lines that should be ignored, such as empty lines, lines with
 * specific prefixes, matching regex patterns, or enclosed between markers. Provides utilities for fine-grained control
 * over which lines of a source are considered relevant for further processing or analysis.
 */

public class TestSourceIgnoredLinesCollector {
    private final String[] originalSource;
    private final List<Integer> relevantLines;

    /**
     * Constructs a collector for the given source lines, initially considering all lines as relevant.
     * @param originalSource the original source lines
     */
    public TestSourceIgnoredLinesCollector(String[] originalSource) {
        this.originalSource = originalSource;
        this.relevantLines = new ArrayList<>();

        for (int i = 0; i < this.originalSource.length; i++) {
            this.relevantLines.add(i + 1);
        }
    }

    /**
     * Ignores all empty or blank lines in the source.
     */
    public void ignoreEmptyLines() {
        this.ignoreByCondition(String::isBlank);
    }

    /**
     * Ignores lines that start with the specified prefix.
     * @param prefix the prefix to match
     */
    public void ignoreLinesByPrefix(String prefix) {
        this.ignoreByCondition(line -> line.trim().startsWith(prefix));
    }

    /**
     * Ignores lines that match the given regular expression. Whitespaces to the left and right of the line will be trimmed
     * first.
     * @param regex The regular expression
     */
    public void ignoreLinesByRegex(String regex) {
        this.ignoreByCondition(line -> line.trim().matches(regex));
    }

    /**
     * Ignores lines that contain the specified content substring.
     * @param content the content to search for
     */
    public void ignoreLinesByContains(String content) {
        this.ignoreByCondition(line -> line.contains(content));
    }

    /**
     * Ignores a block of lines starting with {@code startMarker} and ending with {@code endMarker}.
     * @param startMarker the marker indicating the start of the block
     * @param endMarker the marker indicating the end of the block
     */
    public void ignoreMultipleLines(String startMarker, String endMarker) {
        boolean inMultilineIgnore = false;

        for (int i = 0; i < this.originalSource.length; i++) {
            String line = this.originalSource[i];
            if (!inMultilineIgnore) {
                if (line.trim().startsWith(startMarker)) {
                    this.ignoreByIndex(i);
                    if (!line.trim().substring(startMarker.length() - 1).contains(endMarker)) {
                        inMultilineIgnore = true;
                    }
                }
            } else {
                this.ignoreByIndex(i);
                if (line.contains(endMarker)) {
                    inMultilineIgnore = false;
                }
            }
        }
    }

    /**
     * Ignores a block of lines where the start and end markers are the same.
     * @param startAndEnd the marker indicating both the start and end of the block
     */
    public void ignoreMultipleLines(String startAndEnd) {
        this.ignoreMultipleLines(startAndEnd, startAndEnd);
    }

    /**
     * Ignores lines that satisfy the given condition.
     * @param condition a predicate returning true for lines to ignore
     */
    public void ignoreByCondition(Predicate<String> condition) {
        for (int i = 0; i < this.originalSource.length; i++) {
            if (condition.test(originalSource[i])) {
                ignoreByIndex(i);
            }
        }
    }

    /**
     * Ignores a specific line by its index in the original source.
     * @param index the 0-based index of the line to ignore
     */
    public void ignoreByIndex(int index) {
        this.relevantLines.remove((Object) (index + 1));
    }

    /**
     * Returns the list of relevant line numbers after ignoring operations.
     * @return a list of 1-based line numbers that are not ignored
     */
    public List<Integer> getRelevantLines() {
        return this.relevantLines;
    }
}
