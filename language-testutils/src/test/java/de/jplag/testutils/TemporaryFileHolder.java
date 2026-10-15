package de.jplag.testutils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores all temporary files that are created for a {@link LanguageModuleTest} and provides the option to delete them.
 */
public class TemporaryFileHolder {
    private static List<Path> temporaryFiles = new ArrayList<>();

    /**
     * Deletes all temporary files that have been created up to this point.
     */
    public static void deleteTemporaryFiles() {
        temporaryFiles.forEach(it -> {
            try {
                Files.delete(it);
            } catch (IOException _) {
            }
        });
        temporaryFiles.clear();
    }

    /**
     * Registers a temporary file for later tracking and cleanup.
     * @param file the file to add.
     */
    public static void addTemporaryFile(Path file) {
        temporaryFiles.add(file);
    }
}
