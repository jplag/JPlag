package de.jplag.cli;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.jplag.CommandLineArgument;
import de.jplag.Language;
import de.jplag.LanguageLoader;
import de.jplag.options.JPlagOptions;

class LanguageTest extends CommandLineInterfaceTest {

    @Test
    void testDefaultLanguage() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(JPlagOptions.DEFAULT_LANGUAGE, options.getLanguageShortName());
    }

    @Test
    void testInvalidLanguage() throws Exception {
        String argument = buildArgument(CommandLineArgument.LANGUAGE, "Piet");
        int statusCode = catchSystemExit(() -> buildOptionsFromCLI(argument, CURRENT_DIRECTORY));
        assertEquals(1, statusCode);
    }

    @Test
    void testValidLanguages() {
        for (Language language : LanguageLoader.loadLanguages()) {
            String argument = buildArgument(CommandLineArgument.LANGUAGE, language.getShortName());
            buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
            assertEquals(language.getShortName(), options.getLanguageShortName());
        }
    }

}