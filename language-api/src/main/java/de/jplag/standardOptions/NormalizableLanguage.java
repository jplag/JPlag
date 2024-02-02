package de.jplag.standardOptions;

import de.jplag.Language;
import de.jplag.options.LanguageOptions;

public interface NormalizableLanguage extends Language {
    default NormalizationOptions getNormalizationOptions() {
        if(this instanceof StandardOptionsLanguage) {
            return ((StandardOptionsLanguage) this).normalizationOptions;
        } else {
            throw new IllegalStateException("You can only implement NormalizationLanguage, if you also extend StandardOptionsLanguage");
        }
    }
}
