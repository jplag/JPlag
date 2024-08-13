package de.jplag.testutils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores all temporary files that are created for a {@link LanguageModuleTest} and provides the option to delete them
 */
public class TmpFileHolder {
    public static List<File> tmpFiles = new ArrayList<>();

    /**
     * Deletes all temporary files that have been created up to this point
     */
    public static void deleteTmpFiles() {
        tmpFiles.forEach(File::delete);
        tmpFiles.clear();
    }
}
