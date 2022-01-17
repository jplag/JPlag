package de.jplag;

public interface LanguageProvider {
    Language create(ErrorConsumer errorCollector);
    String getDisplayName();
}
