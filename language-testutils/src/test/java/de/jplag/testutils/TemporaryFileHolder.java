package de.jplag.testutils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores all temporary files that are created for a {@link LanguageModuleTest} and provides the option to delete them.
 */
public class TemporaryFileHolder {
    private static final List<Path> temporaryFiles = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(TemporaryFileHolder.class);

    /**
     * Deletes all temporary files that have been created up to this point.
     */
    public static void deleteTemporaryFiles() {
        temporaryFiles.forEach(it -> {
            try {
                Files.delete(it);
            } catch (IOException _) {
                logger.warn("Cannot delete temporary file. Delete it manually if necessary: {}", it.toAbsolutePath());
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
