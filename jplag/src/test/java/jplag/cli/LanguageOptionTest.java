package jplag.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import jplag.CommandLineArgument;
import jplag.options.LanguageOption;

public class LanguageOptionTest extends CommandLineInterfaceTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testDefaultLanguage() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(LanguageOption.getDefault(), options.getLanguageOption());
    }

    @Test
    public void testInvalidLanguage() {
        exit.expectSystemExitWithStatus(1);
        String argument = buildArgument(CommandLineArgument.LANGUAGE, "Piet");
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
    }

    @Test
    public void testValidLanguages() {
        for (LanguageOption language : LanguageOption.values()) {
            String argument = buildArgument(CommandLineArgument.LANGUAGE, language.getDisplayName());
            buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
            assertEquals(language, options.getLanguageOption());
        }
    }

}
