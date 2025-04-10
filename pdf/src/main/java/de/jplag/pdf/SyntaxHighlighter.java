package de.jplag.pdf;

import com.itextpdf.kernel.colors.Color;

public abstract class SyntaxHighlighter {
    private String[] lines;
    private TextStyleEditor textStyleEditor;

    public SyntaxHighlighter(String[] lines, TextStyleEditor textStyleEditor) {
        this.lines = lines;
        this.textStyleEditor = textStyleEditor;
    }

    protected void highlightKeyword(String keyword, Color color) {
        for (int i = 0; i < this.lines.length; i++) {
            String line = this.lines[i];

            int index = line.indexOf(keyword);
            while (index != -1) {
                this.textStyleEditor.styleRange(i, index, i, index + keyword.length(), null, color);
                index = line.indexOf(keyword, index + 1);
            }
        }
    }

    protected void highlightRanges(String start, String end, Color color) {
        int startCol = 0;
        int startLine = 0;
        boolean foundStart = false;

        int currentLine = 0;
        int currentIndex = -1;

        while (currentLine < this.lines.length) {
            currentIndex = this.lines[currentLine].indexOf(foundStart ? end : start, currentIndex);
            while (currentIndex != -1) {
                if (foundStart) {
                    this.textStyleEditor.styleRange(startLine, startCol, currentLine, currentIndex + end.length(), null, color);
                    foundStart = false;
                } else {
                    startLine = currentLine;
                    startCol = currentIndex;
                    foundStart = true;
                }
                currentIndex = this.lines[currentLine].indexOf(foundStart ? end : start, currentIndex + 1);
            }
            currentLine++;
        }
    }

    protected void highlightToEndOfLine(String start, Color color) {
        for (int i = 0; i < this.lines.length; i++) {
            int index = this.lines[i].indexOf(start);
            if (index != -1) {
                this.textStyleEditor.styleRange(i, index, i, this.lines[i].length(), null, color);
            }
        }
    }

    public abstract void performHighlight();
}
