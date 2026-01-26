package de.jplag.cli.options;

import de.jplag.Language;
import de.jplag.LanguageLoader;

import picocli.CommandLine;

/**
 * Helper class for picocli to convert inputs to languages.
 */
public class LanguageConverter implements CommandLine.ITypeConverter<Language> {
    @Override
    public Language convert(String value) {
        return LanguageLoader.getLanguage(value).orElseThrow();
    }
}
