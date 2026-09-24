package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.cli.options.CliOptions;
import de.jplag.cli.test.ExampleResult;
import de.jplag.exceptions.ExitException;

class OutputFileGeneratorTest {
    private final JPlagResult testResult;

    private OutputFileGeneratorTest() throws IOException, ExitException {
        testResult = ExampleResult.getExampleResult();
    }

    @Test
    void testWriteCsvEnabled() throws IOException {
        File dir = Files.createTempDirectory("jplagCsvTest").toFile();
        CliOptions options = new CliOptions();
        options.advanced.csvExport = true;
        OutputFileGenerator.generateCsvOutput(testResult, dir, options);

        assertEquals(3, Objects.requireNonNull(dir.list()).length);
    }

    @Test
    void testWriteCsvDisabled() throws IOException {
        File dir = Files.createTempDirectory("jplagCsvTest").toFile();
        CliOptions options = new CliOptions();
        options.advanced.csvExport = false;
        OutputFileGenerator.generateCsvOutput(testResult, dir, options);

        assertEquals(0, Objects.requireNonNull(dir.list()).length);
    }

    @Test
    void testWriteCsvNonWritable() throws IOException {
        File dir = Files.createTempDirectory("jplagCsvTest").toFile();
        Assumptions.assumeTrue(dir.setWritable(false));
        CliOptions options = new CliOptions();
        options.advanced.csvExport = true;
        OutputFileGenerator.generateCsvOutput(testResult, dir, options);

        assertEquals(0, Objects.requireNonNull(dir.list()).length);
    }

    @Test
    void testWriteNormalResultFile() throws IOException {
        File resFile = Files.createTempFile("jplagTestResult", ".jplag").toFile();

        OutputFileGenerator.generateJPlagResultFile(testResult, resFile);

        assertTrue(resFile.length() > 0);
    }
}
