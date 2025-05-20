package de.jplag.javascript;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.typescript.TypeScriptLanguage;

/**
 * Represents the JavaScript Language as a variance of TypeScript
 */
@MetaInfServices(Language.class)
public class JavaScriptLanguage extends TypeScriptLanguage {

    @Override
    public String getIdentifier() {
        return "javascript";
    }

    @Override
    public String[] suffixes() {
        return new String[] {".js"};
    }

    @Override
    public String getName() {
        return "JavaScript";
    }

    @Override
    public boolean hasPriority() {
        return false;
    }
}
