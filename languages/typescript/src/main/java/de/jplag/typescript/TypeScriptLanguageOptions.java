package de.jplag.typescript;

import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.options.OptionType;

/**
 * Language Specific options for the TypeScript language
 */
public class TypeScriptLanguageOptions extends LanguageOptions {

    /**
     * Whether the Antlr Grammar should parse
     */
    public LanguageOption<Boolean> useStrictDefault;

    public TypeScriptLanguageOptions() {
        useStrictDefault = createDefaultOption(OptionType.bool(), "useStrictMode", "If set JPlag parses files with the JavaScript strict syntax",
                false);
    }

}
