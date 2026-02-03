package de.jplag.javascript;

import java.util.List;

import de.jplag.Language;
import de.jplag.typescript.TypeScriptLanguage;

import com.google.auto.service.AutoService;

/**
 * Represents the JavaScript Language as a variance of TypeScript. Delegates all responsibility to the TypeScript
 * language.
 */
@AutoService(Language.class)
public class JavaScriptLanguage extends TypeScriptLanguage {

    @Override
    public String getIdentifier() {
        return "javascript";
    }

    @Override
    public List<String> fileExtensions() {
        return List.of(".js");
    }

    @Override
    public String getName() {
        return "JavaScript";
    }

    @Override
    public boolean hasPriority() {
        return false; // Since this module extends Typescript, it has no priority.
    }
}
