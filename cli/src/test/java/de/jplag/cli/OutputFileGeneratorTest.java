package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.cli.options.CliOptions;
import de.jplag.cli.test.ExampleResult;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.FileException;

class OutputFileGeneratorTest {
    private final JPlagResult testResult;

    private OutputFileGeneratorTest() throws IOException, ExitException {
        testResult = ExampleResult.getExampleResult();
    }

    @Test
    void testWriteCsvEnabled() throws IOException {
        Path dir = Files.createTempDirectory("jplagCsvTest");
        CliOptions options = new CliOptions();
        options.advanced.csvExport = true;
        OutputFileGenerator.generateCsvOutput(testResult, dir, options);

        assertEquals(3, Objects.requireNonNull(Files.list(dir)).count());
    }

    @Test
    void testWriteCsvDisabled() throws IOException {
        Path dir = Files.createTempDirectory("jplagCsvTest");
        CliOptions options = new CliOptions();
        options.advanced.csvExport = false;
        OutputFileGenerator.generateCsvOutput(testResult, dir, options);

        assertEquals(0, Objects.requireNonNull(Files.list(dir)).count());
    }

    @Test
    void testWriteCsvNonWritable() throws IOException {
        Path dir = Files.createTempDirectory("jplagCsvTest");
        Files.setPosixFilePermissions(dir, Set.of(PosixFilePermission.OWNER_READ));
        CliOptions options = new CliOptions();
        options.advanced.csvExport = true;
        OutputFileGenerator.generateCsvOutput(testResult, dir, options);

        assertEquals(0, Objects.requireNonNull(Files.list(dir)).count());
    }

    @Test
    void testWriteNormalResultFile() throws IOException, FileException {
        File resFile = Files.createTempFile("jplagTestResult", ".jplag").toFile();

        OutputFileGenerator.generateJPlagResultFile(testResult, resFile);

        assertTrue(resFile.length() > 0);
    }
}
