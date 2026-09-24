package de.jplag.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.ParsingException;

/**
 * Unit tests for {@link FileUtils}.
 */
class FileUtilTest {
    private static final Path TEST_FILE_LOCATION = Path.of("src", "test", "resources", "de", "jplag", "fileReaderTests");
    private static final Path TEST_FILE_SET_LOCATION = Path.of("src", "test", "resources", "de", "jplag", "fileSetEncoding");

    private static final String expectedFileContent = "Some ascii characters and some others: ä#+öü%&(/)?=?";

    @ParameterizedTest
    @MethodSource("searchTestFiles")
    void testReadFile(File file) throws IOException {
        String found = FileUtils.readFileContent(file);

        Assertions.assertEquals(expectedFileContent, found, "File contains unexpected content: " + file.getAbsolutePath());
    }

    @ParameterizedTest
    @MethodSource("searchTestFiles")
    void testCharsetDetection(File file) throws IOException {
        Assertions.assertEquals(Charset.forName(file.getName()), FileUtils.detectCharset(file),
                "Wrong charset assumed for: " + file.getAbsolutePath());
    }

    @Test
    void testDetectFromFileSet() throws ParsingException {
        Set<File> files = Set.of(TEST_FILE_SET_LOCATION.toFile().listFiles());
        Charset encoding = FileUtils.detectCharsetFromMultiple(files);
        Assertions.assertEquals(StandardCharsets.ISO_8859_1, encoding);
    }

    private static File[] searchTestFiles() {
        return TEST_FILE_LOCATION.toFile().listFiles();
    }
}
