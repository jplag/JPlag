package de.jplag.testutils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Test utility regarding files and directories.
 */
public final class FileUtil {

    private static final String CONTENT_NOT_FOUND = "Directory contents not as expected";
    private static final String DIRECTORY_NOT_FOUND = "Could not find directory: ";

    private FileUtil() {
        // private constructor to prevent instantiation
    }

    /**
     * Checks if a directory exists and contains the expected files.
     * @param directory is the directory to check.
     * @param expectedFilesSorted is the sorted list (a to z) of expected file names.
     * @throws IOException If the contents of the directory cannot be listed
     */
    public static void assertDirectory(Path directory, String[] expectedFilesSorted) throws IOException {
        assertTrue(Files.exists(directory), DIRECTORY_NOT_FOUND + directory);
        String[] filesInDirectory = Files.list(directory).map(it -> it.getFileName().toString()).sorted().toArray(String[]::new);
        assertArrayEquals(expectedFilesSorted, filesInDirectory, CONTENT_NOT_FOUND);
    }

    /**
     * Clears all files from a directory that whose names end in a certain extension.
     * @param directory is the target directory.
     * @param extension is the file extension of the files to clear.
     */
    public static void clearFiles(Path directory, String extension) {
        List<Path> files = null;
        try {
            files = Files.list(directory).filter(file -> file.getFileName().toString().endsWith(extension)).toList();

            for (Path file : files) {
                Files.delete(file);
            }
        } catch (IOException _) {
        }
    }

}
