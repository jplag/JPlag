package de.jplag.multilang;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Language;
import de.jplag.LanguageLoader;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.options.LanguageOptions;

@MetaInfServices(Language.class)
public class MultiLanguage implements Language {
    private static final Logger LOG = LoggerFactory.getLogger(MultiLanguage.class);
    private static final String WARNING = "This module only allows parsing of multiple languages. No comparisons will be made between languages";
    private final MultiLanguageOptions options;
    private boolean printedWarning;

    public MultiLanguage() {
        this.options = new MultiLanguageOptions();
        this.printedWarning = false;
    }

    @Override
    public String[] suffixes() {
        return LanguageLoader.getAllAvailableLanguages().values().stream().filter(it -> it != this).flatMap(it -> Arrays.stream(it.suffixes()))
                .toArray(String[]::new);
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
    public boolean supportsMultilanguage() {
        return false;
    }

    private void printWarning() {
        if (!this.printedWarning) {
            this.printedWarning = true;
            LOG.warn(WARNING);
        }
    }
}
