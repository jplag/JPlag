package de.jplag.cli;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.jplag.Language;

class LanguageTest extends CommandLineInterfaceTest {

    @Test
    void testDefaultLanguage() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(CommandLineArgument.DEFAULT_LANGUAGE_IDENTIFIER, options.language().getIdentifier());
    }

    @Test
    void testInvalidLanguage() throws Exception {
        String argument = buildArgument(CommandLineArgument.LANGUAGE, "Piet");
        int statusCode = catchSystemExit(() -> buildOptionsFromCLI(argument, CURRENT_DIRECTORY));
        assertEquals(1, statusCode);
    }

    @Test
    void testLoading() {
        var languages = LanguageLoader.getAllAvailableLanguages();
        assertEquals(14, languages.size(), "Loaded Languages: " + languages.keySet());
    }

    @Test
    void testValidLanguages() {
        for (Language language : LanguageLoader.getAllAvailableLanguages().values()) {
            String argument = buildArgument(CommandLineArgument.LANGUAGE, language.getIdentifier());
            buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
            assertEquals(language.getIdentifier(), options.language().getIdentifier());
            assertEquals(Arrays.asList(language.suffixes()), options.fileSuffixes());
        }
    }

    @Test
    void testCustomSuffixes() {
        List<String> suffixes = List.of("x", "y", "z");
        String argument = buildArgument(CommandLineArgument.SUFFIXES, String.join(",", suffixes));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(suffixes, options.fileSuffixes());
    }

}