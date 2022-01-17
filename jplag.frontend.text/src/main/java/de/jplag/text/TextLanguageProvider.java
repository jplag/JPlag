package de.jplag.text;

import de.jplag.ErrorConsumer;
import de.jplag.Language;
import de.jplag.LanguageProvider;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class TextLanguageProvider implements LanguageProvider {
    public static final String NAME = de.jplag.text.Language.NAME;

    @Override
    public Language create(final ErrorConsumer errorCollector) {
        return new de.jplag.text.Language(errorCollector);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }
}
