package de.jplag.cli;

import static org.junit.Assert.assertEquals;

import de.jplag.Languages;
import de.jplag.java.JavaLanguageProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import de.jplag.CommandLineArgument;

public class LanguageOptionTest extends CommandLineInterfaceTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testDefaultLanguage() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(JavaLanguageProvider.NAME, options.getLanguageName());
    }

    @Test
    public void testInvalidLanguage() {
        exit.expectSystemExitWithStatus(1);
        String argument = buildArgument(CommandLineArgument.LANGUAGE, "Piet");
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
    }

    @Test
    public void testValidLanguages() {
        for (var language : Languages.getAllDisplayNames()) {
            String argument = buildArgument(CommandLineArgument.LANGUAGE, language);
            buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
            assertEquals(language, options.getLanguageName());
        }
    }

}
