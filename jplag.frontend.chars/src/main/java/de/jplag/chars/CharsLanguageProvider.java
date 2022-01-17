package de.jplag.chars;

import de.jplag.ErrorConsumer;
import de.jplag.Language;
import de.jplag.LanguageProvider;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class CharsLanguageProvider implements LanguageProvider {
    public static final String NAME = de.jplag.chars.Language.NAME;

    @Override
    public Language create(final ErrorConsumer errorCollector) {
        return new de.jplag.chars.Language(errorCollector);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }
}
