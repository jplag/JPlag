package de.jplag.cli;

import java.util.ArrayList;

/**
 * Helper class for picocli to find all available languages.
 */
public class LanguageCandidates extends ArrayList<String> {
    public LanguageCandidates() {
        super(LanguageLoader.getAllAvailableLanguageIdentifiers());
    }
}
