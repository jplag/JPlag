package de.jplag.reporting.jsonfactory;

import java.io.File;
import java.io.IOException;

/**
 * Provides Methods for creating directories.
 */
public class DirectoryCreator {

    /**
     * Creates a directory.
     * @param path The path under which the new directory ought to be created
     * @param name The name of the new directory
     * @return The created directory
     */
    public static File createDirectory(String path, String name) throws IOException {
        File directory = new File(path.concat("/").concat(name));
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create dir.");
        }
        return directory;
    }

    /**
     * Create a directory with the given path
     * @param path The path of the new directory
     */
    public static void createDirectory(String path) throws IOException {
        createDirectory(path, "");
    }
}
