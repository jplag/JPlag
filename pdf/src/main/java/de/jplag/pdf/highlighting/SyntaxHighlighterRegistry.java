package de.jplag.pdf.highlighting;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import de.jplag.Language;

public class SyntaxHighlighterRegistry {
    private static SyntaxHighlighterRegistry INSTANCE;

    public static SyntaxHighlighterRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SyntaxHighlighterRegistry();
        }

        return INSTANCE;
    }

    private Map<String, SyntaxHighlighter> suffixesToHighlighter;
    private Map<String, SyntaxHighlighter> languagesToHighlighter;

    public SyntaxHighlighterRegistry() {
        this.suffixesToHighlighter = new HashMap<>();
        this.languagesToHighlighter = new HashMap<>();
        this.loadHighlightersFromFiles();
    }

    public SyntaxHighlighter getSyntaxHighlighterForFile(File file, Language language) {
        String suffix = file.getName().substring(file.getName().lastIndexOf('.') + 1);
        if (this.suffixesToHighlighter.containsKey(suffix)) {
            return this.suffixesToHighlighter.get(suffix);
        }

        if (this.languagesToHighlighter.containsKey(language.getIdentifier())) {
            return this.languagesToHighlighter.get(language.getIdentifier());
        }

        return null; // TODO return dummy highlighter
    }

    private void loadHighlightersFromFiles() {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(SyntaxHighlighterRegistry.class.getResourceAsStream("/highlighting/registry.csv")));
        reader.lines().skip(1).forEach(line -> {
            String[] parts = line.split(",", -1);
            try {
                FileDefinedHighlighter highlighter = new FileDefinedHighlighter(parts[0].trim());
                if (!parts[1].isBlank()) {
                    this.languagesToHighlighter.put(parts[1].trim(), highlighter);
                }

                for (String suffix : parts[2].trim().split(" ")) {
                    suffixesToHighlighter.put(suffix.trim(), highlighter);
                }
            } catch (IOException e) {
                // TODO log
            }
        });
    }
}
