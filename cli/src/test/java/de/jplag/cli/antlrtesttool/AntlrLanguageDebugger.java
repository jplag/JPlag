package de.jplag.cli.antlrtesttool;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Launches the AntlrLanguage used to manually debug antlr language modules.
 */
public class AntlrLanguageDebugger {
    /**
     * Launches the tester
     * @throws IOException -
     */
    @Test
    @Disabled
    void test() throws IOException {
        System.setProperty("awt.useSystemAAFontSettings","on");
        new MainMenu();
    }
}
