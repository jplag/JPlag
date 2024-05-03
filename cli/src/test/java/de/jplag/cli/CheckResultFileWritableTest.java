package de.jplag.cli;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.jplag.cli.picocli.CliInputHandler;

public class CheckResultFileWritableTest extends CommandLineInterfaceTest {
    private static Field inputHandlerField;
    private static Method getWritableFileMethod;

    @BeforeAll
    public static void setup() throws NoSuchFieldException, NoSuchMethodException {
        Class<CLI> cliClass = CLI.class;
        inputHandlerField = cliClass.getDeclaredField("inputHandler");
        getWritableFileMethod = cliClass.getDeclaredMethod("getWritableFileName");

        inputHandlerField.setAccessible(true);
        getWritableFileMethod.setAccessible(true);
    }

    @Test
    public void testNonExistingWritableFile() throws Throwable {
        File directory = Files.createTempDirectory("JPlagTest").toFile();
        File targetFile = new File(directory, "results.zip");

        String path = runResultFileCheck(defaultArguments().resultFile(targetFile.getAbsolutePath()));
        Assertions.assertEquals(targetFile.getAbsolutePath(), path);
    }

    @Test
    public void testNonExistingNotWritableFile() throws IOException {
        File directory = Files.createTempDirectory("JPlagTest").toFile();
        Assumptions.assumeTrue(directory.setWritable(false));
        File targetFile = new File(directory, "results.zip");

        Assertions.assertThrows(CliException.class, () -> {
            runResultFileCheck(defaultArguments().resultFile(targetFile.getAbsolutePath()));
        });
    }

    @Test
    public void testExistingFile() throws Throwable {
        File directory = Files.createTempDirectory("JPlagTest").toFile();
        File targetFile = new File(directory, "results.zip");
        Assumptions.assumeTrue(targetFile.createNewFile());

        String path = runResultFileCheck(defaultArguments().resultFile(targetFile.getAbsolutePath()));
        Assertions.assertEquals(new File(directory, "results(1).zip").getAbsolutePath(), path);
    }

    @Test
    public void testExistingFileOverwrite() throws Throwable {
        File directory = Files.createTempDirectory("JPlagTest").toFile();
        File targetFile = new File(directory, "results.zip");
        Assumptions.assumeTrue(targetFile.createNewFile());

        String path = runResultFileCheck(defaultArguments().resultFile(targetFile.getAbsolutePath()).overwrite());
        Assertions.assertEquals(targetFile.getAbsolutePath(), path);
    }

    @Test
    public void testExistingNotWritableFile() throws IOException {
        File directory = Files.createTempDirectory("JPlagTest").toFile();
        File targetFile = new File(directory, "results.zip");
        Assumptions.assumeTrue(targetFile.createNewFile());
        Assumptions.assumeTrue(targetFile.setWritable(false));

        Assertions.assertThrows(CliException.class, () -> {
            runResultFileCheck(defaultArguments().resultFile(targetFile.getAbsolutePath()).overwrite());
        });
    }

    private String runResultFileCheck(ArgumentBuilder builder) throws Throwable {
        String[] args = builder.getArgumentsAsArray();
        CLI cli = new CLI(args);

        CliInputHandler inputHandler = (CliInputHandler) inputHandlerField.get(cli);
        inputHandler.parse();

        try {
            return (String) getWritableFileMethod.invoke(cli);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
