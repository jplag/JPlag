package de.jplag.end_to_end_testing.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.naming.NameNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagComparison;
import de.jplag.end_to_end_testing.constants.TestDirectoryConstants;
import de.jplag.end_to_end_testing.mapper.LanguageToPathMapper;
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

    private LanguageOption languageOption;
    private String[] resourceNames;
    private Path resultJsonPath;
    private List<JsonModel> resultModel;

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
        this.resourceNames = loadAllTestFileNames(LanguageToPathMapper.getResourcePathsFromLanguageOption(languageOption));
        this.resultJsonPath = LanguageToPathMapper.getTestResultPathFromLanguageOption(languageOption);
        this.resultModel = JsonHelper.getJsonModelListFromPath(resultJsonPath);
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
        JsonModel resultJsonModel = null;
        if (resultModel != null) {
            resultJsonModel = resultModel.stream().filter(jsonModel -> functionName.equals(jsonModel.getFunctionName())).findAny().orElse(null);
        }
        return new TestCaseModel(TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME, resultJsonModel, languageOption);
    }

    /**
     * The copied data should be deleted after instance closure
     * @throws IOException if an I/O error occurs
     */
    public void clear() throws IOException {
        deleteCopiedFiles(new File(TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME));
        logger.debug("Class instance was cleaned!");
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

    /**
     * saves all temporary results for a test into a json file
     * @param jplagComparisonList list of elements to be saved
     * @throws IOException Signals that an I/O exception of some sort has occurred. Thisclass is the general class of
     * exceptions produced by failed orinterrupted I/O operations.
     * @throws NoSuchAlgorithmException when no hash algorithm could be found
     * @throws NameNotFoundException if the no filenames coud be found in the JPlagCOmparison object
     */
    public void saveTemporaryResult(List<JPlagComparison> jplagComparisonList, String functionName)
            throws IOException, NoSuchAlgorithmException, NameNotFoundException {
        for (JPlagComparison jplagComparison : jplagComparisonList) {
            JsonHelper.writeResultModelToJsonFile(new ResultModel(jplagComparison, getTestIdentifier(jplagComparison)),
                    LanguageToPathMapper.getTemporaryResultPathFromLanguageOption(languageOption).toString(), functionName,
                    getTemporaryFileNameForJson(jplagComparison));
        }
    }

    /**
     * Creates a unique identifier from the submissions in the JPlagComparison object which is used to find the results in
     * the json files.
     * @param jPlagComparison object from which the hash should be generated
     * @return unique identifier for test case recognition
     */
    public String getTestIdentifier(JPlagComparison jPlagComparison) {

        String testFileNamesInFirstSubmission = getEnclosedFileNamesFromCollection(jPlagComparison.getFirstSubmission().getFiles());
        String testFileNamesInSecondSubmission = getEnclosedFileNamesFromCollection(jPlagComparison.getSecondSubmission().getFiles());

        int compaire = testFileNamesInFirstSubmission.compareTo(testFileNamesInSecondSubmission);

        String returnIdentifier;

        if (compaire != 0) {
            returnIdentifier = compaire < 0 ? testFileNamesInFirstSubmission + testFileNamesInSecondSubmission
                    : testFileNamesInSecondSubmission + testFileNamesInFirstSubmission;
        } else {
            returnIdentifier = testFileNamesInFirstSubmission + testFileNamesInSecondSubmission;
        }

        return returnIdentifier;
    }

    /**
     * Merges all contained filenames together without extension
     * @param files whose names are to be merged
     * @return merged filenames
     */
    private String getEnclosedFileNamesFromCollection(Collection<File> files) {
        StringBuilder stringBuilder = new StringBuilder(files.size());
        for (File file : files) {
            String fileName = file.getName();
            stringBuilder.append(fileName.substring(0, fileName.lastIndexOf('.')));
        }

        return stringBuilder.toString();
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
     * Creates a transitional name from the tested file names to store the test results temporarily
     * @param jplagComparison for which a temporary name with the extension .json is to be created
     * @return temporary storage name for a json file
     * @throws FileNotFoundException if no suitable file name could be found
     * @throws NameNotFoundException if the no filenames coud be found in the JPlagCOmparison object
     */
    private String getTemporaryFileNameForJson(JPlagComparison jplagComparison) throws FileNotFoundException, NameNotFoundException {
        // load submission via stream into variable to get the names from the submission object
        var firstSubmissionStream = jplagComparison.getFirstSubmission().getFiles().stream().findFirst();
        var secondSubmissionStream = jplagComparison.getSecondSubmission().getFiles().stream().findFirst();
        // if the stream for the comparison objects contains a name, this name is written as string into the new variables
        String fileNameFirstSubmission = firstSubmissionStream.isPresent() ? firstSubmissionStream.get().getName() : null;
        String fileNameSecondSubmission = secondSubmissionStream.isPresent() ? secondSubmissionStream.get().getName() : null;
        // if one of the name fields is empty an exception is thrown
        if (fileNameFirstSubmission == null || fileNameSecondSubmission == null) {
            String message = fileNameFirstSubmission == null ? "fileNameFromFirstSubmission is null" : "";
            message += message.isBlank() && fileNameSecondSubmission == null ? "" : " and ";
            message += fileNameSecondSubmission == null ? "fileNameFromSecondSubmission is null" : "";
            throw new NameNotFoundException(message);
        }
        // remove file extension
        fileNameFirstSubmission = fileNameFirstSubmission.substring(0, fileNameFirstSubmission.lastIndexOf('.'));
        fileNameSecondSubmission = fileNameSecondSubmission.substring(0, fileNameSecondSubmission.lastIndexOf('.'));
        return fileNameFirstSubmission + "_" + fileNameSecondSubmission + ".json";
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
