package de.jplag.java;

import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.options.OptionType;

public class JavaLanguageOptions extends LanguageOptions {
    public final LanguageOption<Boolean> normalize = createDefaultOption(OptionType.bool(), "normalize", "Enable token string normalization.", false);
}
