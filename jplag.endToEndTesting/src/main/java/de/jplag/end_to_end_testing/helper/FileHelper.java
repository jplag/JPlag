package de.jplag.end_to_end_testing.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.jplag.end_to_end_testing.constants.TestDirectoryConstants;
import de.jplag.options.LanguageOption;

/**
 * Helper class to perform all necessary operations or functions on files or folders.
 */
public class FileHelper {

    private FileHelper() {
        // private constructor to prevent instantiation
    }

    /**
     * Merges all contained filenames together without extension
     * @param files whose names are to be merged
     * @return merged filenames
     */
    public static String getEnclosedFileNamesFromCollection(Collection<File> files) {
        StringBuilder stringBuilder = new StringBuilder(files.size());
        for (File file : files) {
            String fileName = file.getName();
            stringBuilder.append(fileName.substring(0, fileName.lastIndexOf('.')));
        }

        return stringBuilder.toString();
    }

    /**
     * Load all possible language is resource path
     * @param directoryNames folder names for which the language options should be listed.
     * @return list of all LanguageOptions included in the resource path
     */
    public static List<LanguageOption> getLanguageOptionsFromPath(String[] directoryNames) {
        List<LanguageOption> returnList = new ArrayList<>();
        for (String languageDirectoryName : directoryNames) {
            returnList.add(LanguageOption.valueOf(languageDirectoryName));
        }
        return returnList;
    }

    /**
     * @param directorieRoot path from which all folders should be loaded
     * @return all folders found in the specified path
     * @throws IOException
     */
    public static String[] getAllDirectoriesInPath(Path directorieRoot) throws IOException {
        return directorieRoot.toFile().list((dir, name) -> new File(dir, name).isDirectory());
    }

    /**
     * Copies the passed filenames to a temporary path to use them in the tests
     * @param classNames for which the test case is to be created
     * @return paths created to the test submissions
     * @throws IOException Exception can be thrown in cases that involve reading, copying or locating files.
     */
    public static String[] createNewTestCaseDirectory(String[] classNames) throws IOException {
        // Copy the resources data to the temporary path
        String[] returnSubmissionPath = new String[classNames.length];
        for (int counter = 0; counter < classNames.length; counter++) {
            Path originalPath = Path.of(classNames[counter]);
            returnSubmissionPath[counter] = Path
                    .of(TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME.toString(), "submission" + (counter + 1)).toAbsolutePath()
                    .toString();
            Path copiePath = Path.of(TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME.toString(), "submission" + (counter + 1),
                    originalPath.getFileName().toString());

            File directory = new File(copiePath.toString());
            if (!directory.exists()) {
                directory.mkdirs();
            }
            Files.copy(originalPath, copiePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return returnSubmissionPath;
    }

    /**
     * Delete directory with including files
     * @param folder Path to a folder or file to be deleted. This happens recursively to the path
     * @throws IOException if an I/O error occurs
     */
    public static void deleteCopiedFiles(File folder) throws IOException {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) { // some JVMs return null for empty dirs
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteCopiedFiles(file);
                    } else {
                        Files.delete(file.toPath());
                    }
                }
            }
            Files.delete(folder.toPath());
        }
    }

    /**
     * Creates directory if it dose not exist
     * @param directory to be created
     * @throws IOException if the directory could not be created
     */
    public static void createDirectoryIfItDoseNotExist(File directory) throws IOException {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException(createNewIOExceptionStringForFileOrFOlderCreation(directory));
        }
    }

    /**
     * Creates file if it dose not exist
     * @param file to be created
     * @throws IOException if the file could not be created
     */
    public static void createFileIfItDoseNotExist(File file) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException(createNewIOExceptionStringForFileOrFOlderCreation(file));
        }
    }

    /**
     * @param resourcenPaths list of paths that lead to test resources
     * @return all filenames contained in the paths
     */
    public static String[] loadAllTestFileNames(Path resourcenPaths) {
        var files = resourcenPaths.toFile().listFiles();
        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }
        return fileNames;
    }

    /**
     * @param file for which the exception text is to be created
     * @return exception text for the specified file
     */
    private static String createNewIOExceptionStringForFileOrFOlderCreation(File file) {
        return "The file/folder at the location [" + file.toString() + "] could not be created!";
    }
}
