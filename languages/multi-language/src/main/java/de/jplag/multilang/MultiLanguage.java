package de.jplag.multilang;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Language;
import de.jplag.LanguageLoader;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.options.LanguageOptions;

import com.google.auto.service.AutoService;

/**
 * Multi-language facade. Delegates all source code files of known languages to the corresponding concrete language
 * modules.
 */
@AutoService(Language.class)
public class MultiLanguage implements Language {
    private static final Logger logger = LoggerFactory.getLogger(MultiLanguage.class);
    private static final String WARNING = "This module only allows parsing of multiple languages. No comparisons will be made between languages";
    private final MultiLanguageOptions options;
    private boolean printedWarning;

    /**
     * Creates the multi-language facade.
     */
    public MultiLanguage() {
        this.options = new MultiLanguageOptions();
        this.printedWarning = false;
    }

    @Override
    public List<String> fileExtensions() {
        return LanguageLoader.getAllAvailableLanguages().values().stream().filter(it -> it != this).flatMap(it -> it.fileExtensions().stream())
                .toList();
    }

    @Override
    public String getName() {
        return "multi-language";
    }

    @Override
    public String getIdentifier() {
        return "multi";
    }

    @Override
    public int minimumTokenMatch() {
        return this.options.getLanguages().stream().mapToInt(Language::minimumTokenMatch).min().orElse(9);
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        this.printWarning();
        MultiLanguageParser parser = new MultiLanguageParser(this.options);
        return parser.parseFiles(files, normalize);
    }

    @Override
    public LanguageOptions getOptions() {
        return this.options;
    }

    @Override
    public boolean supportsMultiLanguage() {
        return false;
    }

    private void printWarning() {
        if (!this.printedWarning) {
            this.printedWarning = true;
            logger.warn(WARNING);
        }
    }
}
