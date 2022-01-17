package de.jplag.scheme;

import de.jplag.ErrorConsumer;
import de.jplag.Language;
import de.jplag.LanguageProvider;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class SchemeLanguageProvider implements LanguageProvider {

    public static final String NAME = de.jplag.scheme.Language.NAME;

    @Override
    public Language create(final ErrorConsumer errorCollector) {
        return new de.jplag.scheme.Language(errorCollector);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }
}
