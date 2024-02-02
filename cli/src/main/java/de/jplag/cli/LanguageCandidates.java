package de.jplag.cli;

import java.util.ArrayList;

/**
 * Helper class for picocli to find all available languages.
 */
public class LanguageCandidates extends ArrayList<String> {
    /**
     * Creates a new instance. Should only be called automatically by picocli and never manually.
     */
    public LanguageCandidates() {
        super(LanguageLoader.getAllAvailableLanguageIdentifiers());
    }
}
