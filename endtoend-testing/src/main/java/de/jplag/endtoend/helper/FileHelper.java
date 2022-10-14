package de.jplag.endtoend.helper;

import java.io.File;
import java.io.IOException;

/**
 * Helper class to perform all necessary operations or functions on files or folders.
 */
public class FileHelper {

    private FileHelper() {
        // private constructor to prevent instantiation
    }

    /**
     * Returns the name of the passed file, trimming its file extension.
     * @param file is the file to obtain the name from
     * @return returns the name of the file without file extension
     */
    public static String getFileNameWithoutFileExtension(File file) {
        String name = file.getName();
        int index = name.lastIndexOf('.');
        return index == -1 ? name : name.substring(0, index);
    }

    /**
     * Creates directory if it dose not exist
     * @param directory to be created
     * @throws IOException if the directory could not be created
     */
    public static void createDirectoryIfItDoesNotExist(File directory) throws IOException {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException(createNewIOExceptionStringForFileOrFOlderCreation(directory));
        }
    }

    /**
     * Creates file if it dose not exist
     * @param file to be created
     * @throws IOException if the file could not be created
     */
    public static void createFileIfItDoesNotExist(File file) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException(createNewIOExceptionStringForFileOrFOlderCreation(file));
        }
    }

    /**
     * @param file for which the exception text is to be created
     * @return exception text for the specified file
     */
    private static String createNewIOExceptionStringForFileOrFOlderCreation(File file) {
        return "The file/folder at the location [" + file.toString() + "] could not be created!";
    }
}
