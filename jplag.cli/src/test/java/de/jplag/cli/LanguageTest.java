package de.jplag.cli;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.CommandLineArgument;
import de.jplag.Language;
import de.jplag.LanguageLoader;

class LanguageTest extends CommandLineInterfaceTest {

    @Test
    void testDefaultLanguage() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(CommandLineArgument.DEFAULT_LANGUAGE_IDENTIFIER, options.getLanguageIdentifier());
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
        Assertions.assertEquals(13, languages.size(), "Loaded Languages: " + languages.keySet());
    }

    @Test
    void testValidLanguages() {
        for (Language language : LanguageLoader.getAllAvailableLanguages().values()) {
            String argument = buildArgument(CommandLineArgument.LANGUAGE, language.getIdentifier());
            buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
            assertEquals(language.getIdentifier(), options.getLanguageIdentifier());
        }
    }

}