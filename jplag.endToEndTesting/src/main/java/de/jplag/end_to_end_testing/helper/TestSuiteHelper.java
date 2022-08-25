package de.jplag.end_to_end_testing.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.end_to_end_testing.constants.TestDirectoryConstants;

public class TestSuiteHelper {

    /**
     * private constructor to prevent instantiation
     */
    private TestSuiteHelper() {
        // For Serialization
    }

    /**
     * Loads all existing test data into a test structure. the complex structure consists of the LanguageOption and their
     * data to be tested. These are divided into folders. for more information please read the README file
     * @return mapped LanguageOption to the data under test
     */
    public static Map<String, Map<String, Path>> getAllLanguageResources() {
        String[] languageDirectoryNames = FileHelper.getAllDirectoriesInPath(TestDirectoryConstants.BASE_PATH_TO_LANGUAGE_RESOURCES);
        List<String> languageInPathList = FileHelper.getLanguageOptionsFromPath(languageDirectoryNames);

        Map<String, Map<String, Path>> returnMap = new HashMap<>();

        for (String languageIdentifier : languageInPathList) {
            var tempMap = new HashMap<String, Path>();
            var allDirectoriesInPath = FileHelper
                    .getAllDirectoriesInPath(Path.of(TestDirectoryConstants.BASE_PATH_TO_LANGUAGE_RESOURCES.toString(), languageIdentifier));
            Arrays.asList(allDirectoriesInPath).forEach(directory -> tempMap.put(Path.of(directory).toFile().getName(),
                    Path.of(TestDirectoryConstants.BASE_PATH_TO_LANGUAGE_RESOURCES.toString(), languageIdentifier, directory)));
            returnMap.put(languageIdentifier, tempMap);
        }
        return returnMap;
    }

    /**
     * Creates a unique identifier from the submissions in the JPlagComparison object which is used to find the results in
     * the json files.
     * @param jPlagComparison object from which the hash should be generated
     * @return unique identifier for test case recognition
     */
    public static String getTestIdentifier(JPlagComparison jPlagComparison) {

        return List.of(jPlagComparison.getFirstSubmission(), jPlagComparison.getSecondSubmission()).stream().map(Submission::getFiles)
                .map(FileHelper::getEnclosedFileNamesFromCollection).sorted().collect(Collectors.joining("-"));

    }

    /**
     * Creates the permutation of all data contained in the passed parameters and adds it to the given path.
     * @param fileNames for which the permutations are needed
     * @param path to which the permutations are to be copied
     * @return all permutations of the specified files to the path specified
     */
    public static List<String[]> getTestCases(String[] fileNames, Path path) {
        ArrayList<String[]> testCases = new ArrayList<>();
        int outerCounter = 1;
        for (String fileName : fileNames) {
            for (int counter = outerCounter; counter < fileNames.length; counter++) {
                testCases.add(new String[] {Path.of(path.toAbsolutePath().toString(), fileName).toString(),
                        Path.of(path.toAbsolutePath().toString(), fileNames[counter]).toString()});
            }
            outerCounter++;
        }
        return testCases;
    }

    /**
     * The copied data should be deleted after instance closure
     * @throws IOException if an I/O error occurs
     */
    public static void clear() throws IOException {
        FileHelper.deleteCopiedFiles(new File(TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME.toString()));
    }
}
