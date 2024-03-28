package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.Language;

class LanguageTest extends CommandLineInterfaceTest {

    @Test
    void testDefaultLanguage() throws CliException {
        buildOptionsFromCLI(defaultArguments());
        assertEquals(CliOptions.defaultLanguage.getIdentifier(), options.language().getIdentifier());
    }

    @Test
    void testInvalidLanguage() {
        Assertions.assertThrowsExactly(CliException.class, () -> buildOptionsFromCLI(defaultArguments().language("Piet")));
    }

    @Test
    void testLoading() {
        var languages = LanguageLoader.getAllAvailableLanguages();
        assertEquals(20, languages.size(), "Loaded Languages: " + languages.keySet());
    }

    @Test
    void testValidLanguages() throws CliException {
        for (Language language : LanguageLoader.getAllAvailableLanguages().values()) {
            buildOptionsFromCLI(defaultArguments().language(language.getIdentifier()));

            assertEquals(language.getIdentifier(), options.language().getIdentifier());
            assertEquals(Arrays.asList(language.suffixes()), options.fileSuffixes());
        }
    }

    @Test
    void testCustomSuffixes() throws CliException {
        String[] suffixes = {"x", "y", "z"};
        buildOptionsFromCLI(defaultArguments().suffixes(suffixes));
        assertEquals(List.of(suffixes), options.fileSuffixes());
    }

}
