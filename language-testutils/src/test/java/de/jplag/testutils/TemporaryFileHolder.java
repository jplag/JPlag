package de.jplag.testutils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores all temporary files that are created for a {@link LanguageModuleTest} and provides the option to delete them.
 */
public class TemporaryFileHolder {
    public static List<File> temporaryFiles = new ArrayList<>();

    /**
     * Deletes all temporary files that have been created up to this point.
     */
    public static void deleteTemporaryFiles() {
        temporaryFiles.forEach(File::delete);
        temporaryFiles.clear();
    }
}
