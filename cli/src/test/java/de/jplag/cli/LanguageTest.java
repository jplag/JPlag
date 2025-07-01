package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.Language;
import de.jplag.LanguageLoader;
import de.jplag.cli.options.CliOptions;
import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.multilang.MultiLanguage;
import de.jplag.options.JPlagOptions;

class LanguageTest extends CliTest {
    private static final List<Class<? extends Language>> ignoredLanguages = List.of(MultiLanguage.class);

    @Test
    void testDefaultLanguage() throws ExitException, IOException {
        JPlagOptions options = runCliForOptions();
        assertEquals(CliOptions.defaultLanguage.getIdentifier(), options.language().getIdentifier());
    }

    @Test
    void testInvalidLanguage() {
        Assertions.assertThrowsExactly(CliException.class, () -> {
            runCli(args -> args.with(CliArgument.LANGUAGE, "Piet"));
        });
    }

    @Test
    void testLoading() {
        var languages = LanguageLoader.getAllAvailableLanguages();
        assertEquals(20, languages.size(), "Loaded Languages: " + languages.keySet());
    }

    @ParameterizedTest
    @MethodSource("getAllLanguages")
    void testValidLanguages(Language language) throws ExitException, IOException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.LANGUAGE, language.getIdentifier()));

        assertEquals(language.getIdentifier(), options.language().getIdentifier());
        assertEquals(language.fileExtensions(), options.fileSuffixes());
    }

    @Test
    void testCustomSuffixes() throws ExitException, IOException {
        String[] suffixes = {"x", "y", "z"};
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SUFFIXES, suffixes));
        assertEquals(List.of(suffixes), options.fileSuffixes());
    }

    public static Collection<Language> getAllLanguages() {
        return LanguageLoader.getAllAvailableLanguages().values().stream().filter(language -> !ignoredLanguages.contains(language.getClass()))
                .toList();
    }
}
