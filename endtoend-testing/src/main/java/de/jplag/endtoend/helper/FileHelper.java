package de.jplag.endtoend.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

    public static void unzip(File zip, File targetDirectory) throws IOException {
        ZipFile zipFile = new ZipFile(zip);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                new File(targetDirectory, entry.getName()).mkdirs();
            } else {
                File outputFile = new File(targetDirectory, entry.getName());
                outputFile.getParentFile().mkdirs();

                InputStream inputStream = zipFile.getInputStream(entry);
                OutputStream outputStream = new FileOutputStream(outputFile);
                inputStream.transferTo(outputStream);
                inputStream.close();
                outputStream.close();
            }
        }
        zipFile.close();
    }
}
