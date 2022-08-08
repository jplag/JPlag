package de.jplag.end_to_end_testing.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import de.jplag.JPlagComparison;
import de.jplag.end_to_end_testing.constants.TestDirectoryConstants;
import de.jplag.end_to_end_testing.model.JsonModel;
import de.jplag.end_to_end_testing.model.ResultModel;
import de.jplag.end_to_end_testing.model.TestCaseModel;
import de.jplag.options.LanguageOption;

/**
 * This helper class deals with creating the test cases as well as copying and deleting for the end-to-end tests. The
 * required plagiarisms are copied from the resource folder to a temporary location, thus creating a folder structure
 * that can be tested by JPlag. Models are instantiated here and required information for the tests is loaded.
 */
public class JPlagTestSuiteHelper {

    private static final Logger logger = LoggerFactory.getLogger(JPlagTestSuiteHelper.class);

    private String[] resourceNames;
    private List<JsonModel> resultModel;
    private LanguageOption languageOption;
    private Path resultJsonPath;

    /**
     * Helper class for the endToEnd tests. In this class the necessary resources are loaded, prepared and copied for the
     * tests based on the passed parameters. An instance of this class loads all necessary paths and properties for a test
     * run with the specified language
     * @param languageOption for loading language-specific resources
     * @throws IOException is thrown for all problems that may occur while parsing the json file. This includes both reading
     * and parsing problems.
     */
    public JPlagTestSuiteHelper(LanguageOption languageOption) throws IOException {
        this.languageOption = languageOption;
        this.resourceNames = loadAllTestFileNames(TestDirectoryConstants.RESOURCE_PATH_MAPPER().get(languageOption));
        this.resultJsonPath = TestDirectoryConstants.RESULT_PATH_MAPPER().get(languageOption);
        this.resultModel = JsonHelper.getResultModelFromPath(resultJsonPath);

        logger.debug("temp path at [{}]", TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME);
    }

    /**
     * creates all necessary folder paths and objects for a test run. Also searches for the stored previous results of the
     * test in order to compare them with the current results.
     * @param classNames Array of class names with language specific extension to be prepared for a test.
     * @return comparison results saved for the test
     * @throws IOException Exception can be thrown in cases that involve reading, copying or locating files.
     */
    public TestCaseModel createNewTestCase(String[] classNames, String functionName) throws IOException {
        createNewTestCaseDirectory(classNames);
        JsonModel resultJsonModel = resultModel.stream().filter(jsonModel -> functionName.equals(jsonModel.getFunctionName())).findAny().orElse(null);
        return new TestCaseModel(TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME, resultJsonModel, languageOption);
    }

    /**
     * The copied data should be deleted after instance closure
     * @throws IOException if an I/O error occurs
     */
    public void clear() throws IOException {
        logger.debug("Class instance was cleaned!");
        deleteCopiedFiles(new File(TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME));
    }

    /**
     * @return Path to the stored test results
     */
    public Path getResultJsonPath() {
        return resultJsonPath;
    }

    /**
     * @return all resources names for the current specified language
     */
    public String[] getAllTestFileNames() {
        return resourceNames;
    }

    public void saveResult(List<JPlagComparison> jplagComparisonList) throws StreamWriteException, DatabindException, IOException {
        for (JPlagComparison jplagComparison : jplagComparisonList) {
            JsonHelper.writeObjectToJsonFile(new ResultModel(jplagComparison, getTestHashCode(jplagComparison)),
                    TestDirectoryConstants.TEMPORARY_RESULT_DIRECTORY_NAME);
        }
    }

    /**
     * Creates a unique hash from the submissions in the JPlagComparison object which is used to find the results in the
     * json files.
     * @param jPlagComparison object from which the hash should be generated
     * @return unique identifier for test case recognition
     */
    public int getTestHashCode(JPlagComparison jPlagComparison) {
        String testFileNamesInFirstSubmission = new String();
        String testFileNamesInSecondSubmission = new String();
        for (File file : jPlagComparison.getFirstSubmission().getFiles()) {
            testFileNamesInFirstSubmission += file.getName().toString();
        }
        for (File file : jPlagComparison.getSecondSubmission().getFiles()) {
            testFileNamesInSecondSubmission += file.getName().toString();
        }
        return (testFileNamesInFirstSubmission + testFileNamesInSecondSubmission).hashCode();
    }

    /**
     * @param resourcenPaths list of paths that lead to test resources
     * @return all filenames contained in the paths
     */
    private String[] loadAllTestFileNames(List<Path> resourcenPaths) {
        ArrayList<String> temporaryResourceNames = new ArrayList<>();
        for (Path path : resourcenPaths) {
            temporaryResourceNames.addAll(Arrays.asList(path.toFile().list()));
        }

        return temporaryResourceNames.toArray(new String[temporaryResourceNames.size()]);
    }

    /**
     * Copies the passed filenames to a temporary path to use them in the tests
     * @throws IOException Exception can be thrown in cases that involve reading, copying or locating files.
     */
    private void createNewTestCaseDirectory(String[] classNames) throws IOException {
        // before copying files to the test path, check if all files are in the resource
        // directory
        for (String className : classNames) {
            if (!Arrays.asList(resourceNames).contains(className)) {
                throw new FileNotFoundException(String.format("The specified class could not be found! [%s]", className));
            }
        }
        // Copy the resources data to the temporary path
        for (int counter = 0; counter < classNames.length; counter++) {
            Path originalPath = Path.of(TestDirectoryConstants.BASE_PATH_TO_JAVA_RESOURCES_SORTALGO.toString(), classNames[counter]);
            Path copiePath = Path.of(TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME, "submission" + (counter + 1), classNames[counter]);

            File directory = new File(copiePath.toString());
            if (!directory.exists()) {
                directory.mkdirs();
            }

            Files.copy(originalPath, copiePath, StandardCopyOption.REPLACE_EXISTING);
            logger.debug("Copy file from [{}] to [{}]", originalPath, copiePath);
        }
    }

    /**
     * Delete directory with including files
     * @param file Path to a folder or file to be deleted. This happens recursively to the path
     * @throws IOException if an I/O error occurs
     */
    private void deleteCopiedFiles(File folder) throws IOException {
        File[] files = folder.listFiles();
        if (files != null) { // some JVMs return null for empty dirs
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteCopiedFiles(file);
                } else {
                    Files.delete(file.toPath());
                    logger.debug("Delete file in folder: [{}]", file);
                }
            }
        }
        Files.delete(folder.toPath());
        logger.debug("Delete folder: [{}]", folder);
    }

}
