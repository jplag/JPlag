package de.jplag.regressiontest.helper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Helper class to perform all necessary operations or functions on files or folders.
 */
public final class FileHelper {
    private static final int ZIP_THRESHOLD_ENTRIES = 100000;
    private static final int ZIP_THRESHOLD_SIZE = 1000000000;
    private static final double ZIP_THRESHOLD_RATIO = 10;
    private static final String ZIP_BOMB_ERROR_MESSAGE = "Refusing to unzip file (%s), because it seems to be a fork bomb";

    private FileHelper() {
        // private constructor to prevent instantiation
    }

    /**
     * Returns the name of the passed file, trimming its file extension.
     * @param file is the file to obtain the name from
     * @return returns the name of the file without file extension
     */
    public static String getFileNameWithoutFileExtension(Path file) {
        String name = file.getFileName().toString();
        int index = name.lastIndexOf('.');
        return index == -1 ? name : name.substring(0, index);
    }

    /**
     * Creates directory if it does not exist.
     * @param directory to be created
     * @throws IOException if the directory could not be created
     */
    public static void createDirectoryIfItDoesNotExist(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
    }

    /**
     * Creates file if it does not exist.
     * @param file to be created
     * @throws IOException if the file could not be created
     */
    public static void createFileIfItDoesNotExist(Path file) throws IOException {
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
    }

    /**
     * @param file for which the exception text is to be created
     * @return exception text for the specified file
     */
    private static String createNewIOExceptionStringForFileOrFOlderCreation(File file) {
        return "The file/folder at the location [" + file.toString() + "] could not be created!";
    }

    /**
     * Unzips a given zip file into a given directory.
     * @param zip The zip file to extract
     * @param targetDirectory The target directory
     * @throws IOException If io operations go wrong
     * @throws IllegalStateException if the ZIP archive exceeds size or entry count thresholds.
     */
    public static void unzip(Path zip, Path targetDirectory) throws IOException { // TODO replace by nio virtual file system?
        try (ZipInputStream in = new ZipInputStream(Files.newInputStream(zip))) {
            ZipEntry entry;

            long totalSizeArchive = 0;
            long totalEntriesArchive = 0;

            while ((entry = in.getNextEntry()) != null) {
                totalEntriesArchive++;

                Path targetFile = targetDirectory.resolve(entry.getName());
                if (targetFile.startsWith(targetDirectory)) {
                    if (entry.isDirectory()) {
                        Files.createDirectories(targetFile);
                    } else {
                        Files.createDirectories(targetFile.getParent());
                        totalSizeArchive += extractZipElement(entry, in, zip, targetFile);
                    }
                }

                if (totalSizeArchive > ZIP_THRESHOLD_SIZE || totalEntriesArchive > ZIP_THRESHOLD_ENTRIES) {
                    throw new IllegalStateException(String.format(ZIP_BOMB_ERROR_MESSAGE, zip));
                }
            }
        }
    }

    private static long extractZipElement(ZipEntry entry, ZipInputStream zipFile, Path zip, Path target) throws IOException {
        long totalSizeEntry = 0;

        try (OutputStream outputStream = Files.newOutputStream(target)) {
            byte[] buffer = new byte[2048];
            int count;
            while ((count = zipFile.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);

                totalSizeEntry += count;

                double compressionRate = (double) totalSizeEntry / entry.getCompressedSize();
                if (compressionRate > ZIP_THRESHOLD_RATIO) {
                    throw new IllegalStateException(String.format(ZIP_BOMB_ERROR_MESSAGE, zip));
                }
            }
        }

        return totalSizeEntry;
    }
}