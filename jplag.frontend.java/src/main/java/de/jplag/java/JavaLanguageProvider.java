package de.jplag.java;

import de.jplag.ErrorConsumer;
import de.jplag.Language;
import de.jplag.LanguageProvider;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class JavaLanguageProvider implements LanguageProvider {
    public static final String NAME = de.jplag.java.Language.NAME;

    @Override
    public Language create(final ErrorConsumer errorCollector) {
        return new de.jplag.java.Language(errorCollector);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }
}
