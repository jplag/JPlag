package de.jplag.multilang;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * Routes files to the appropriate {@link Language} parser in a multi-language context. Honors language priority and
 * file extension uniqueness.
 */
public class MultiLanguageParser {
    private static final String ERROR_MULTIPLE_LANGUAGES = "The suffix %s appears for multiple languages (%s, %s) with same priority setting. This is not permitted as it causes ambiguities for the multi-language module.";

    private final Map<String, Language> languageMapPriority;
    private final Map<String, Language> languageMap;

    /**
     * Initializes the parser with the provided multi-language configuration.
     * @param options is the configuration.
     */
    public MultiLanguageParser(MultiLanguageOptions options) {
        this.languageMapPriority = new HashMap<>();
        this.languageMap = new HashMap<>();
        this.registerLanguageMaps(options.getLanguages());
    }

    /**
     * Parses the given files using appropriate language parsers.
     * @param files the files to parse
     * @param normalize whether to normalize tokens
     * @return a list of parsed tokens
     * @throws ParsingException if parsing fails
     */
    public List<Token> parseFiles(Set<File> files, boolean normalize) throws ParsingException {
        List<Token> results = new ArrayList<>();
        for (File file : files) {
            Optional<Language> language = findLanguageForFile(file);
            if (language.isPresent()) {
                results.addAll(language.get().parse(Set.of(file), normalize));
            }
        }
        return results;
    }

    private void registerLanguageMaps(List<Language> languages) {
        for (Language language : languages.stream().sorted(Comparator.comparing(it -> it.hasPriority() ? 1 : 0)).toList()) {
            for (String suffix : language.fileExtensions()) {
                registerLanguageSuffix(language, suffix);
            }

        }
    }

    private void registerLanguageSuffix(Language language, String suffix) {
        if (language.hasPriority()) {
            registerLanguageSuffixTo(this.languageMapPriority, language, suffix, true);
        } else {
            registerLanguageSuffixTo(this.languageMap, language, suffix, !this.languageMapPriority.containsKey(suffix));
        }
    }

    private void registerLanguageSuffixTo(Map<String, Language> map, Language language, String suffix, boolean additionalConditionForException) {
        if (map.containsKey(suffix) && additionalConditionForException) {
            throw new IllegalStateException(String.format(ERROR_MULTIPLE_LANGUAGES, suffix, language.getName(), map.get(suffix).getName()));
        }
        map.put(suffix, language);
    }

    private Optional<Language> findLanguageForFile(File file) {
        String name = file.getName();
        String extension = name.substring(name.lastIndexOf('.')).toLowerCase();

        if (this.languageMapPriority.containsKey(extension)) {
            return Optional.of(this.languageMapPriority.get(extension));
        }

        if (this.languageMap.containsKey(extension)) {
            return Optional.of(this.languageMap.get(extension));
        }

        return Optional.empty();
    }
}
