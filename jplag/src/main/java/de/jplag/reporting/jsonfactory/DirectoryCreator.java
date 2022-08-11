package de.jplag.reporting.jsonfactory;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryCreator {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryCreator.class);

    public static File createDirectory(String path, String name) {
        File directory = new File(path.concat("/").concat(name));
        if (!directory.exists() && !directory.mkdirs()) {
            logger.error("Failed to create dir.");
        }
        return directory;
    }

    public static void createDirectory(String path) {
        createDirectory(path, "");
    }
}
