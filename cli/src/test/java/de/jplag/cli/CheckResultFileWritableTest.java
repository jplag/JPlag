package de.jplag.cli;

import static de.jplag.cli.test.CliArgument.OVERWRITE_RESULT_FILE;
import static de.jplag.cli.test.CliArgument.RESULT_FILE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import de.jplag.cli.test.CliTest;

class CheckResultFileWritableTest extends CliTest {
    @Test
    void testNonExistingWritableFile() throws Throwable {
        File directory = Files.createTempDirectory("JPlagTest").toFile();
        File targetFile = new File(directory, "results.jplag");

        String path = runCliForTargetPath(args -> args.with(RESULT_FILE, targetFile.getAbsolutePath()));
        Assertions.assertEquals(targetFile.getAbsolutePath(), path);
    }

    @Test
    void testNonExistingNotWritableFile() throws IOException {
        File directory = Files.createTempDirectory("JPlagTest").toFile();
        Assumptions.assumeTrue(directory.setWritable(false));
        Assumptions.assumeFalse(directory.canWrite());
        File targetFile = new File(directory, "results.jplag");

        Assertions.assertThrows(CliException.class, () -> {
            runCli(args -> args.with(RESULT_FILE, targetFile.getAbsolutePath()));
        });
    }

    @Test
    void testExistingFile() throws Throwable {
        File directory = Files.createTempDirectory("JPlagTest").toFile();
        File targetFile = new File(directory, "results.jplag");
        Assumptions.assumeTrue(targetFile.createNewFile());

        String path = runCliForTargetPath(args -> args.with(RESULT_FILE, targetFile.getAbsolutePath()));
        Assertions.assertEquals(new File(directory, "results(1).jplag").getAbsolutePath(), path);
    }

    @Test
    void testExistingFileOverwrite() throws Throwable {
        File directory = Files.createTempDirectory("JPlagTest").toFile();
        File targetFile = new File(directory, "results.jplag");
        Assumptions.assumeTrue(targetFile.createNewFile());

        String path = runCliForTargetPath(args -> args.with(RESULT_FILE, targetFile.getAbsolutePath()).with(OVERWRITE_RESULT_FILE));
        Assertions.assertEquals(targetFile.getAbsolutePath(), path);
    }

    @Test
    void testExistingNotWritableFile() throws IOException {
        File directory = Files.createTempDirectory("JPlagTest").toFile();
        File targetFile = new File(directory, "results.jplag");
        Assumptions.assumeTrue(targetFile.createNewFile());
        Assumptions.assumeTrue(targetFile.setWritable(false));
        Assumptions.assumeFalse(targetFile.canWrite());

        Assertions.assertThrows(CliException.class, () -> {
            runCli(args -> args.with(OVERWRITE_RESULT_FILE).with(RESULT_FILE, targetFile.getAbsolutePath()));
        });
    }
}
