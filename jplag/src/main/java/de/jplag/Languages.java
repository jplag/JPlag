package de.jplag;

import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class Languages {
    public static List<String> getAllDisplayNames() {
        return providers().stream()
                .map(LanguageProvider::getDisplayName)
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<LanguageProvider> providers() {
        List<LanguageProvider> services = new ArrayList<>();
        var loader = ServiceLoader.load(LanguageProvider.class);
        loader.forEach(services::add);
        return services;
    }

    public static LanguageProvider provider(String languageName) {
        return providers().stream()
                .filter(p -> p.getDisplayName().equals(languageName))
                .findAny()
                .orElseThrow(() -> languageProviderNotfound(languageName));
    }

    private static ProviderNotFoundException languageProviderNotfound(String languageName) {
        final var message = String.format("Language Provider for language %s not found", languageName);
        return new ProviderNotFoundException(message);
    }

    public static Language loadLanguage(final ErrorCollector errorCollector, final String classPath) {
        return Languages.provider(classPath).create(errorCollector);
    }
}
