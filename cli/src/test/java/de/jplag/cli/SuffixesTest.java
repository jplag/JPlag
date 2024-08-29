package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

class SuffixesTest extends CliTest {
    private static final List<String> JAVA_SUFFIXES = List.of(".java", ".JAVA");
    private static final List<String> CUSTOM_SUFFIXES = List.of(".j", ".jva");

    @Test
    void testDefaultSuffixes() throws IOException, ExitException {
        JPlagOptions options = runCliForOptions();
        assertEquals(JAVA_SUFFIXES, options.fileSuffixes());
    }

    @Test
    void testSetSuffixes() throws IOException, ExitException {
        JPlagOptions options = runCliForOptions(args -> args.with(CliArgument.SUFFIXES, CUSTOM_SUFFIXES.toArray(new String[0])));
        assertEquals(CUSTOM_SUFFIXES, options.fileSuffixes());
    }
}
