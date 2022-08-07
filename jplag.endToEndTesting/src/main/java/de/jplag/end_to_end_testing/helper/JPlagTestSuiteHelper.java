package de.jplag.end_to_end_testing.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.end_to_end_testing.constants.Constant;
import de.jplag.end_to_end_testing.model.JsonModel;
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
        this.resourceNames = new File(Constant.BASE_PATH_TO_JAVA_RESOURCES_SORTALGO.toString()).list();

        this.resultModel = JsonHelper.getResultModelFromPath();
        logger.info("temp path at [{}]", Constant.TEMPORARY_SUBMISSION_DIRECTORY_NAME);
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
        return new TestCaseModel(Constant.TEMPORARY_SUBMISSION_DIRECTORY_NAME, resultJsonModel, languageOption);
    }

    /**
     * The copied data should be deleted after instance closure
     * @throws IOException if an I/O error occurs
     */
    public void clear() throws IOException {
        logger.info("Class instance was cleaned!");
        deleteCopiedFiles(new File(Constant.TEMPORARY_SUBMISSION_DIRECTORY_NAME));
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
            Path originalPath = Path.of(Constant.BASE_PATH_TO_JAVA_RESOURCES_SORTALGO.toString(), classNames[counter]);
            Path copiePath = Path.of(Constant.TEMPORARY_SUBMISSION_DIRECTORY_NAME, Constant.TEMPORARY_DIRECTORY_NAME + (counter + 1),
                    classNames[counter]);

            File directory = new File(copiePath.toString());
            if (!directory.exists()) {
                directory.mkdirs();
            }

            Files.copy(originalPath, copiePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Copy file from [{}] to [{}]", originalPath, copiePath);
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
                    logger.info("Delete file in folder: [{}]", file);
                }
            }
        }
        Files.delete(folder.toPath());
        logger.info("Delete folder: [{}]", folder);
    }

}
