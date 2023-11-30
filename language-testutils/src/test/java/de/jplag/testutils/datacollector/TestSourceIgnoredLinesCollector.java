package de.jplag.testutils.datacollector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TestSourceIgnoredLinesCollector {
    private final String[] originalSource;
    private final List<Integer> relevantLines;

    public TestSourceIgnoredLinesCollector(String[] originalSource) {
        this.originalSource = originalSource;
        this.relevantLines = new ArrayList<>();

        for (int i = 0; i < this.originalSource.length; i++) {
            this.relevantLines.add(i + 1);
        }
    }

    public void ignoreEmptyLines() {
        this.ignoreByCondition(String::isBlank);
    }

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

    public void ignoreLinesByContains(String content) {
        this.ignoreByCondition(line -> line.contains(content));
    }

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

    public void ignoreMultipleLines(String startAndEnd) {
        this.ignoreMultipleLines(startAndEnd, startAndEnd);
    }

    public void ignoreByCondition(Predicate<String> condition) {
        for (int i = 0; i < this.originalSource.length; i++) {
            if (condition.test(originalSource[i])) {
                ignoreByIndex(i);
            }
        }
    }

    public void ignoreByIndex(int index) {
        this.relevantLines.remove((Object) (index + 1));
    }

    public List<Integer> getRelevantLines() {
        return this.relevantLines;
    }
}
