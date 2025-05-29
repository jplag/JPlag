package de.jplag.multilang;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

public class MultiLanguageParser {
    private final List<Language> languages;

    public MultiLanguageParser(MultiLanguageOptions options) {
        this.languages = options.getLanguages();
    }

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

    private Optional<Language> findLanguageForFile(File file) {
        return this.languages.stream().filter(language -> language.fileExtensions().stream() //
                .anyMatch(extension -> file.getName().endsWith(extension))).findFirst();

    }
}
