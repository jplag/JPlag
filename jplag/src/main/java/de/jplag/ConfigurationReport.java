package de.jplag;

import de.jplag.options.JPlagOptions;

import java.util.Set;

public class ConfigurationReport {
    private final JPlagOptions options;
    private final Language language;
    private final Set<String> excludedFileNames;
    private final String[] fileSuffixes;
    private final int minimumTokenMatch;

    public ConfigurationReport(JPlagOptions options, Language language, Set<String> excludedFileNames, String[] fileSuffixes, int minimumTokenMatch) {
        this.options = options;
        this.language = language;
        this.excludedFileNames = excludedFileNames;
        this.fileSuffixes = fileSuffixes;
        this.minimumTokenMatch = minimumTokenMatch;
    }

    public JPlagOptions getOptions() {
        return options;
    }

    public Language getLanguage() {
        return language;
    }

    public Set<String> getExcludedFileNames() {
        return excludedFileNames;
    }

    public String[] getFileSuffixes() {
        return fileSuffixes;
    }

    public int getMinimumTokenMatch() {
        return minimumTokenMatch;
    }
}
