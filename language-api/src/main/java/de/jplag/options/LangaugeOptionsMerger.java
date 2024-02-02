package de.jplag.options;

public class LangaugeOptionsMerger extends LanguageOptions {
    public void merge(LanguageOptions options) {
        this.options.addAll(options.options);
    }
}
