package de.jplag.multilang;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.LanguageLoader;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.options.LanguageOptions;

@MetaInfServices(Language.class)
public class MultiLanguage implements Language {
    private final MultiLanguageOptions options;

    public MultiLanguage() {
        this.options = new MultiLanguageOptions();
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
        MultiLanguageParser parser = new MultiLanguageParser(this.options);
        return parser.parseFiles(files, normalize);
    }

    @Override
    public LanguageOptions getOptions() {
        return this.options;
    }
}
