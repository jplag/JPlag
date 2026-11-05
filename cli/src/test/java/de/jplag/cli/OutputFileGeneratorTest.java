package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.cli.options.CliOptions;
import de.jplag.cli.test.ExampleResult;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.FileException;
import de.jplag.util.FilePermissionUtils;

class OutputFileGeneratorTest {
    private final JPlagResult testResult;

    private OutputFileGeneratorTest() throws IOException, ExitException {
        testResult = ExampleResult.getExampleResult();
    }

    @Test
    void testWriteCsvEnabled() throws IOException {
        Path dir = FilePermissionUtils.createTempDirectory("jplagCsvTest");
        CliOptions options = new CliOptions();
        options.advanced.csvExport = true;
        OutputFileGenerator.generateCsvOutput(testResult, dir, options);

        assertEquals(3, Objects.requireNonNull(Files.list(dir)).count());
    }

    @Test
    void testWriteCsvDisabled() throws IOException {
        Path dir = FilePermissionUtils.createTempDirectory("jplagCsvTest");
        CliOptions options = new CliOptions();
        options.advanced.csvExport = false;
        OutputFileGenerator.generateCsvOutput(testResult, dir, options);

        assertEquals(0, Objects.requireNonNull(Files.list(dir)).count());
    }

    @Test
    void testWriteCsvNonWritable() throws IOException {
        Path dir = FilePermissionUtils.createTempDirectory("jplagCsvTest");
        FilePermissionUtils.removeWriteAccess(dir);
        CliOptions options = new CliOptions();
        options.advanced.csvExport = true;
        OutputFileGenerator.generateCsvOutput(testResult, dir, options);

        assertEquals(0, Objects.requireNonNull(Files.list(dir)).count());
    }

    @Test
    void testWriteNormalResultFile() throws IOException, FileException {
        Path resFile = Files.createTempFile("jplagTestResult", ".jplag");

        OutputFileGenerator.generateJPlagResultFile(testResult, resFile);

        assertTrue(Files.size(resFile) > 0);
    }
}
