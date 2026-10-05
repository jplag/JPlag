package de.jplag.pdf.highlighting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.math3.util.Pair;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;

public class FileDefinedHighlighter implements SyntaxHighlighter {
    private final Map<String, Color> keywordColors;
    private final Map<Pair<String, String>, Color> rangeColors;
    private final Map<String, Color> toEndOfLineColors;

    public FileDefinedHighlighter(String name) throws IOException {
        super();

        this.keywordColors = new HashMap<>();
        iterateOverFile("/highlighting/" + name + "/keywords.csv", line -> {
            this.keywordColors.put(line[0].trim(), getColor(line[1]));
        });

        this.rangeColors = new HashMap<>();
        iterateOverFile("/highlighting/" + name + "/ranges.csv", line -> {
            this.rangeColors.put(new Pair<>(line[0].trim(), line[1].trim()), getColor(line[2]));
        });

        this.toEndOfLineColors = new HashMap<>();
        iterateOverFile("/highlighting/" + name + "/toEndOfLine.csv", line -> {
            this.toEndOfLineColors.put(line[0].trim(), getColor(line[1]));
        });
    }

    private Color getColor(String definition) {
        if (definition.trim().startsWith("#")) {
            int r = Integer.parseInt(definition.trim().substring(1, 3), 16);
            int g = Integer.parseInt(definition.trim().substring(3, 5), 16);
            int b = Integer.parseInt(definition.trim().substring(5, 7), 16);
            return new DeviceRgb(r, g, b);
        } else {
            return SyntaxHighlighterColors.valueOf(definition.trim().toUpperCase()).getColor();
        }
    }

    private void iterateOverFile(String name, Consumer<String[]> lineParser) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FileDefinedHighlighter.class.getResourceAsStream(name)))) {
            reader.lines().skip(1).forEach(line -> lineParser.accept(line.split(",")));
        }
    }

    @Override
    public void performHighlight(SyntaxHighlightPerformer performer) {
        this.keywordColors.forEach(performer::highlightKeyword);

        this.rangeColors.forEach((range, color) -> performer.highlightRanges(range.getFirst(), range.getSecond(), color));

        this.toEndOfLineColors.forEach(performer::highlightToEndOfLine);
    }
}
