package de.jplag.cli;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.jplag.CommandLineArgument;
import de.jplag.options.LanguageOption;

public class LanguageOptionTest extends CommandLineInterfaceTest {

    @Test
    public void testDefaultLanguage() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(LanguageOption.getDefault(), options.getLanguageOption());
    }

    @Test
    public void testInvalidLanguage() throws Exception {
        String argument = buildArgument(CommandLineArgument.LANGUAGE, "Piet");
        int statusCode = catchSystemExit(() -> buildOptionsFromCLI(argument, CURRENT_DIRECTORY));
        assertEquals(1, statusCode);
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
