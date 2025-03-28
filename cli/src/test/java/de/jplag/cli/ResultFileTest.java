package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliArgument;
import de.jplag.cli.test.CliTest;
import de.jplag.exceptions.ExitException;

class ResultFileTest extends CliTest {
    private static final String DEFAULT_RESULT_FILE = "results.zip";
    private static final String TEST_RESULT_FILE = "customResults.zip";
    private static final String TEST_RESULT_FILE_WITH_AVOIDANCE = "customResults(1).zip";
    private static final String TEST_RESULT_FILE_WITHOUT_ZIP = "customResults";

    @Test
    void testDefaultResultFolder() throws IOException, ExitException {
        String targetPath = runCliForTargetPath();
        assertEquals(DEFAULT_RESULT_FILE, targetPath);
    }

    @Test
    void testSetResultFolder() throws IOException, ExitException {
        String targetPath = runCliForTargetPath(args -> args.with(CliArgument.RESULT_FILE, TEST_RESULT_FILE));
        assertEquals(TEST_RESULT_FILE, targetPath);
    }

    @Test
    void testSetResultFolderWithoutZip() throws IOException, ExitException {
        String targetPath = runCliForTargetPath(args -> args.with(CliArgument.RESULT_FILE, TEST_RESULT_FILE_WITHOUT_ZIP));
        assertEquals(TEST_RESULT_FILE, targetPath);
    }

    @Test
    void testResultFileOverrideAvoidance() throws IOException, ExitException {
        File testDir = Files.createTempDirectory("JPlagResultFileTest").toFile();
        File targetFile = new File(testDir, TEST_RESULT_FILE);
        File expectedTargetFile = new File(testDir, TEST_RESULT_FILE_WITH_AVOIDANCE);
        targetFile.createNewFile();

        String actualTargetPath = runCliForTargetPath(args -> args.with(CliArgument.RESULT_FILE, targetFile.getAbsolutePath()));

        targetFile.delete();
        testDir.delete();

        assertEquals(expectedTargetFile.getAbsolutePath(), actualTargetPath);
    }

    @Test
    void testResultFileOverwrite() throws IOException, ExitException {
        File testDir = Files.createTempDirectory("JPlagResultFileTest").toFile();
        File targetFile = new File(testDir, TEST_RESULT_FILE);
        targetFile.createNewFile();

        String actualTargetPath = runCliForTargetPath(
                args -> args.with(CliArgument.RESULT_FILE, targetFile.getAbsolutePath()).with(CliArgument.OVERWRITE_RESULT_FILE, true));

        targetFile.delete();
        testDir.delete();

        assertEquals(targetFile.getAbsolutePath(), actualTargetPath);
    }
}
