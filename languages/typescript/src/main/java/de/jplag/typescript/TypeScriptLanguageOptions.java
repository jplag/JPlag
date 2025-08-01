package de.jplag.typescript;

import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.options.OptionType;

/**
 * Language Specific options for the TypeScript language.
 */
public class TypeScriptLanguageOptions extends LanguageOptions {

    /**
     * Whether the Antlr Grammar should parse.
     */
    private final LanguageOption<Boolean> useStrictDefault = createDefaultOption(OptionType.bool(), "useStrictMode",
            "If set JPlag parses files with the JavaScript strict syntax", false);

    /**
     * Returns whether strict mode parsing is enabled.
     * @return {@code true} if strict mode is used; {@code false} otherwise.
     */
    public boolean useStrictDefault() {
        return this.useStrictDefault.getValue();
    }

}
