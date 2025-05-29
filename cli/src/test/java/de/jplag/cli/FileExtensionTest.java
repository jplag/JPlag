package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.java.JavaLanguage;
import de.jplag.options.JPlagOptions;

class FileExtensionTest extends CliTest {
    private static final List<String> CUSTOM_EXTENSIONS = List.of(".j", ".jva");

    @Test
    void testDefaultExtensions() throws IOException, ExitException {
        JPlagOptions options = runCliForOptions();

        List<String> expectedExtensions = new JavaLanguage().fileExtensions();
        assertIterableEquals(expectedExtensions, options.fileSuffixes());
    }

    @Test
    void testCustomExtensions() throws IOException, ExitException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SUFFIXES, CUSTOM_EXTENSIONS.toArray(new String[0])));
        assertEquals(CUSTOM_EXTENSIONS, options.fileSuffixes());
    }
}
