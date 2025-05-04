package de.jplag.multilang;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

public class MultiLanguageParser {
    private static final Logger LOG = LoggerFactory.getLogger(MultiLanguageParser.class);
    private static final String WARNING = "This module only allows parsing of multiple languages. No comparisons will be made between languages";
    private static final String ERROR_MULTIPLE_PRIORITY_LANGUAGES = "Multiple language modules with priority (%s) have been found for file: %s";
    private static final String ERROR_MULTIPLE_LANGUAGES = "Multiple language modules (%s) have been found for file: %s";
    private final List<Language> languages;
    private static boolean hasPrintedWarning;

    public MultiLanguageParser(MultiLanguageOptions options) {
        this.languages = options.getLanguages();
        hasPrintedWarning = false;
    }

    public List<Token> parseFiles(Set<File> files, boolean normalize) throws ParsingException {
        this.printWarning();
        List<Token> results = new ArrayList<>();
        for (File file : files) {
            Optional<Language> language = findLanguageForFile(file);
            if (language.isPresent()) {
                results.addAll(language.get().parse(Set.of(file), normalize));
            }
        }
        return results;
    }

    private Optional<Language> findLanguageForFile(File file) throws ParsingException {
        List<Language> normalLanguages = new ArrayList<>();
        List<Language> priorityLanguages = new ArrayList<>();

        for (Language language : this.languages) {
            if (Arrays.stream(language.suffixes()).anyMatch(it -> file.getName().toLowerCase().endsWith(it.toLowerCase()))) {
                if (language.hasPriority()) {
                    priorityLanguages.add(language);
                } else {
                    normalLanguages.add(language);
                }
            }
        }

        if (!priorityLanguages.isEmpty()) {
            if (priorityLanguages.size() > 1) {
                throw new ParsingException(file, String.format(ERROR_MULTIPLE_PRIORITY_LANGUAGES,
                        String.join(", ", priorityLanguages.stream().map(Language::getName).toList()), file.getPath()));
            }

            return Optional.of(priorityLanguages.getFirst());
        } else {
            if (normalLanguages.isEmpty()) {
                return Optional.empty();
            }

            if (normalLanguages.size() > 1) {
                throw new ParsingException(file, String.format(ERROR_MULTIPLE_LANGUAGES,
                        String.join(", ", normalLanguages.stream().map(Language::getName).toList()), file.getPath()));
            }

            return Optional.of(normalLanguages.getFirst());
        }
    }

    private void printWarning() {
        if (!hasPrintedWarning) {
            hasPrintedWarning = true;
            LOG.warn(WARNING);
        }
    }
}
