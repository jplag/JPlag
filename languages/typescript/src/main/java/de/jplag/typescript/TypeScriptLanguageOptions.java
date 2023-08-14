package de.jplag.typescript;

import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.options.OptionType;

public class TypeScriptLanguageOptions extends LanguageOptions {

    public LanguageOption<Boolean> useStrictDefault;

    public TypeScriptLanguageOptions() {
        useStrictDefault = createDefaultOption(OptionType.bool(), "useStrictMode", "If set JPlag parses files with the JavaScript strict syntax", false);
    }

}
