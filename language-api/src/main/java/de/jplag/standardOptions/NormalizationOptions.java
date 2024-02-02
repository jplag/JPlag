package de.jplag.standardOptions;

import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.options.OptionType;

public class NormalizationOptions extends LanguageOptions {
    public LanguageOption<Boolean> normalize = createDefaultOption(OptionType.bool(), "normalize", "Enables the token normalization.", false);
}
