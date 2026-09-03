package de.jplag.pdf;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;

public class TextStyleEditor {
    private List<StyledLine> lines;
    private static Color veryLightGray = Color.createColorWithColorSpace(new float[] {.9f, .9f, .9f});

    public TextStyleEditor(String[] lines) {
        this.lines = new ArrayList<>();
        for (String line : lines) {
            this.lines.add(new StyledLine(line));
        }
    }

    public void addTo(Cell cell, PdfFont font, Color defaultBackground) {
        for (StyledLine line : this.lines) {
            Paragraph paragraph = new Paragraph();
            paragraph.setFont(font).setBackgroundColor(defaultBackground);
            paragraph.setPadding(0f).setMargin(0f).setFixedLeading(17f);
            line.addTo(paragraph);

            Color background = line.getLastBackgroundColor();
            paragraph.setBackgroundColor(background != null ? background : veryLightGray);

            cell.add(paragraph);
        }
    }

    public void addTo(Table table, PdfFont font) {
        int numberOfDigits = (this.lines.isEmpty()) ? 1 : (int) Math.log10(this.lines.size()) + 1;

        for (int i = 0; i < this.lines.size(); i++) {
            StyledLine line = this.lines.get(i);
            Paragraph paragraph = new Paragraph();
            paragraph.setFont(font);
            paragraph.setPadding(0f).setMargin(0f).setFixedLeading(15f);
            line.addTo(paragraph);

            Color background = line.getLastBackgroundColor();
            paragraph.setBackgroundColor(background != null ? background : veryLightGray);

            String lineNumber = String.valueOf(i);
            table.addCell(new Cell().setBorder(null).setMargin(0).setPadding(0)
                    .add(new Paragraph("\u200b" + " ".repeat(numberOfDigits - lineNumber.length()) + lineNumber + " ").setFixedLeading(15f))
                    .setFont(font));
            table.addCell(new Cell().setBorder(null).setMargin(0).setPadding(0).add(paragraph));
        }
    }

    public void styleRange(int startLine, int startChar, int endLine, int endChar, Color background, Color foreground) {
        if (startLine == endLine) {
            this.lines.get(startLine).styleRange(startChar, endChar, background, foreground);
        } else {
            this.lines.get(startLine).styleRange(startChar, this.lines.get(startLine).getLength(), background, foreground);
            this.lines.get(endLine).styleRange(0, endChar, background, foreground);

            for (int i = startLine + 1; i < endLine; i++) {
                StyledLine line = this.lines.get(i);
                line.styleRange(0, line.getLength(), background, foreground);
            }
        }
    }

    private class StyledLine {
        private List<StyledText> chunks;
        private int length;

        public StyledLine(String line) {
            this.chunks = new ArrayList<>();
            this.chunks.add(new StyledText(line, null, null));
            this.length = line.length();
        }

        void styleRange(int from, int until, Color background, Color foreground) {
            if (from >= this.length) {
                from = this.length - 1;
            }
            if (until > this.length) {
                until = this.length;
            }

            int firstChunk, lastChunk = -1;

            int currentIndex = 0;
            int currentChunk = 0;
            while (currentIndex < from) {
                if (from - currentIndex < this.chunks.get(currentChunk).getLength()) {
                    int splitPoint = from - currentIndex;
                    StyledText current = this.chunks.get(currentChunk);
                    this.chunks.set(currentChunk, current.leftSubText(splitPoint));
                    this.chunks.add(currentChunk + 1, current.rightSubText(splitPoint));
                }

                currentIndex += this.chunks.get(currentChunk).getLength();
                currentChunk++;
            }

            firstChunk = currentChunk;

            while (currentIndex < until) {
                if (until - currentIndex < this.chunks.get(currentChunk).getLength()) {
                    int splitPoint = until - currentIndex;
                    StyledText current = this.chunks.get(currentChunk);
                    this.chunks.set(currentChunk, current.leftSubText(splitPoint));
                    this.chunks.add(currentChunk + 1, current.rightSubText(splitPoint));
                }

                currentIndex += this.chunks.get(currentChunk).getLength();
                currentChunk++;
            }

            lastChunk = currentChunk - 1;

            for (int i = firstChunk; i <= lastChunk; i++) {
                if (foreground != null) {
                    this.chunks.get(i).setForeground(foreground);
                }
                if (background != null) {
                    this.chunks.get(i).setBackground(background);
                }
            }

            if (this.length == 0) {
                if (foreground != null) {
                    this.chunks.getFirst().setForeground(foreground);
                }
                if (background != null) {
                    this.chunks.getFirst().setBackground(background);
                }
            }
        }

        public int getLength() {
            return length;
        }

        public void addTo(Paragraph paragraph) {
            paragraph.add("\u200b");
            for (StyledText chunk : this.chunks) {
                chunk.addTo(paragraph);
            }
            paragraph.add(" \u200b");
        }

        public Color getLastBackgroundColor() {
            return this.chunks.getLast().background;
        }

        public Color getFirstBackgroundColor() {
            return this.chunks.getFirst().background;
        }
    }

    private class StyledText {
        String text;
        Color background;
        Color foreground;

        public StyledText(String text, Color background, Color foreground) {
            this.text = text;
            this.background = background;
            this.foreground = foreground;
        }

        public void setBackground(Color background) {
            this.background = background;
        }

        public void setForeground(Color foreground) {
            this.foreground = foreground;
        }

        public int getLength() {
            return this.text.length();
        }

        public StyledText leftSubText(int splitPoint) {
            return new StyledText(this.text.substring(0, splitPoint), this.background, this.foreground);
        }

        public StyledText rightSubText(int splitPoint) {
            return new StyledText(this.text.substring(splitPoint), this.background, this.foreground);
        }

        public void addTo(Paragraph paragraph) {
            Text text = new Text(this.text);
            text.addStyle(new Style().setBackgroundColor(background != null ? background : veryLightGray).setFontColor(foreground));
            text.setFontSize(10.3f);
            paragraph.add(text);
        }
    }
}
