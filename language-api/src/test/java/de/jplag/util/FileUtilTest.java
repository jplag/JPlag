package de.jplag.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
    void testReadFile(Path file) throws IOException {
        String found = FileUtils.readFileContent(file);

        Assertions.assertEquals(expectedFileContent, found, "File contains unexpected content: " + file.toRealPath());
    }

    @ParameterizedTest
    @MethodSource("searchTestFiles")
    void testCharsetDetection(Path file) throws IOException {
        Assertions.assertEquals(Charset.forName(file.getFileName().toString()), FileUtils.detectCharset(file),
                "Wrong charset assumed for: " + file.toRealPath());
    }

    @Test
    void testDetectFromFileSet() throws ParsingException, IOException {
        List<Path> files = Files.list(TEST_FILE_LOCATION).toList();
        Charset encoding = FileUtils.detectCharsetFromMultiple(files);
        Assertions.assertEquals(StandardCharsets.ISO_8859_1, encoding);
    }

    private static Path[] searchTestFiles() throws IOException {
        return Files.list(TEST_FILE_LOCATION).toArray(Path[]::new);
    }
}
