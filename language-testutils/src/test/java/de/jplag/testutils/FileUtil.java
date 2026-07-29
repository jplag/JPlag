package de.jplag.testutils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;

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
     */
    public static void assertDirectory(File directory, String[] expectedFilesSorted) {
        assertTrue(directory.exists(), DIRECTORY_NOT_FOUND + directory);
        String[] filesInDirectory = directory.list();
        Arrays.sort(filesInDirectory);
        assertArrayEquals(expectedFilesSorted, filesInDirectory, CONTENT_NOT_FOUND);
    }

    /**
     * Clears all files from a directory that whose names end in a certain extension.
     * @param directory is the target directory.
     * @param extension is the file extension of the files to clear.
     */
    public static void clearFiles(File directory, String extension) {
        Arrays.stream(directory.listFiles()).filter(file -> file.getName().endsWith(extension)).forEach(File::delete);
    }

}
