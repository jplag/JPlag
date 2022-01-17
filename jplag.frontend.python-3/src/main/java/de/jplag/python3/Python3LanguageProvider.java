package de.jplag.python3;

import de.jplag.ErrorConsumer;
import de.jplag.Language;
import de.jplag.LanguageProvider;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class Python3LanguageProvider implements LanguageProvider {

    public static final String NAME = de.jplag.python3.Language.NAME;

    @Override
    public Language create(final ErrorConsumer errorCollector) {
        return new de.jplag.python3.Language(errorCollector);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }
}
