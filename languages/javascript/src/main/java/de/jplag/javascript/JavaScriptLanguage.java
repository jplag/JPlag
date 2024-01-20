package de.jplag.javascript;

import de.jplag.typescript.TypeScriptLanguage;
import org.kohsuke.MetaInfServices;

/**
 * Represents the JavaScript Language as a variance of TypeScript
 */
@MetaInfServices(de.jplag.Language.class)
public class JavaScriptLanguage extends TypeScriptLanguage {

    private static final String IDENTIFIER = "javascript";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String[] suffixes() {
        return new String[] {".js"};
    }
}
