package de.jplag.standardOptions;

import de.jplag.Language;
import de.jplag.options.LangaugeOptionsMerger;
import de.jplag.options.LanguageOptions;

public abstract class StandardOptionsLanguage implements Language {
    NormalizationOptions normalizationOptions = new NormalizationOptions();

    @Override
    public LanguageOptions mixinOptions() {
        LangaugeOptionsMerger merger = new LangaugeOptionsMerger();
        merger.merge(getOptions());

        if(this instanceof NormalizableLanguage) {
            merger.merge(this.normalizationOptions);
        }

        return merger;
    }
}
