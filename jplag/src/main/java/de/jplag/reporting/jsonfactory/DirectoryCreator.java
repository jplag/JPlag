package de.jplag.reporting.jsonfactory;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryCreator {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryCreator.class);

    /**
     * Creates a directory.
     * @param path The path under which the new directory ought to be created
     * @param name The name of the new directory
     * @return The created directory
     */
    public static File createDirectory(String path, String name) {
        File directory = new File(path.concat("/").concat(name));
        if (!directory.exists() && !directory.mkdirs()) {
            logger.error("Failed to create dir.");
        }
        return directory;
    }

    /**
     * Create a directory with the given path
     * @param path The path of the new directory
     */
    public static void createDirectory(String path) {
        createDirectory(path, "");
    }
}
