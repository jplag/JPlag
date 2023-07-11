package de.jplag.typescript;

import de.jplag.options.DefaultLanguageOption;
import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.options.OptionType;

public class TypeScriptLanguageOptions extends LanguageOptions {

    public LanguageOption<Boolean> useStrictDefault;

    public TypeScriptLanguageOptions() {
        useStrictDefault = createDefaultOption(OptionType.bool(), "useStrictMode", "Runs JPlag with the JavaScript strict syntax", false);
    }

}
