package de.jplag.endToEndTesting.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.FilerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.endToEndTesting.constants.Constant;
import de.jplag.options.LanguageOption;
import model.ResultJsonModel;
import model.TestCaseModel;

public class JPlagTestSuiteHelper {

	private static final Logger logger = LoggerFactory.getLogger("EndToEndTesting");

	private String[] resourceNames;
	private String temporaryFolderPath;
	private List<ResultJsonModel> resultModel;
	private LanguageOption languageOption;

	/**
	 * Helper class for the endToEnd tests. In this class the necessary resources
	 * are loaded, prepared and copied for the tests based on the passed parameters.
	 * An instance of this class loads all necessary paths and properties for a test
	 * run with the specified language
	 * 
	 * @param languageOption for loading language-specific resources
	 * @throws Exception
	 */
	public JPlagTestSuiteHelper(LanguageOption languageOption) throws Exception {
		this.languageOption = languageOption;
		this.resourceNames = new File(Constant.BASE_PATH_TO_JAVA_RESOURCES_SORTALGO.toString()).list();
		this.temporaryFolderPath = getTempFolderPath();

		this.resultModel = JsonHelper
				.getResultModelFromPath(Constant.BASE_PATH_TO_JAVA_RESULT_JSON.toAbsolutePath().toString());
		logger.info(String.format("temp path at [%s]", this.temporaryFolderPath));
	}

	/**
	 * creates all necessary folder paths and objects for a test run.
	 * 
	 * @param classNames
	 * @return
	 * @throws Exception
	 */
	public TestCaseModel createNewTestCase(String[] classNames) throws Exception {
		createNewTestCaseDirectory(classNames);
		var functionName = StackWalker.getInstance().walk(stream -> stream.skip(1).findFirst().get()).getMethodName();
		ResultJsonModel resultJsonModel = resultModel.stream()
				.filter(jsonModel -> functionName.equals(jsonModel.getFunctionName())).findAny().orElse(null);
		return new TestCaseModel(temporaryFolderPath, resultJsonModel, languageOption);
	}

	/**
	 * Loads a suitable system path to temporarily store the test cases.
	 * 
	 * @return The temporary system folder, if any, or the path of the current
	 *         runtime environment
	 * @throws IOException
	 */
	private String getTempFolderPath() throws IOException {
		return !System.getProperty(Constant.TEMP_SYSTEM_DIRECTORY).isBlank()
				? Path.of(System.getProperty(Constant.TEMP_SYSTEM_DIRECTORY), Constant.TEMP_DIRECTORY_NAME).toString()
				: Path.of(new File(".").getCanonicalPath(), Constant.TEMP_DIRECTORY_NAME).toString();
	}

	/**
	 * The copied data should be deleted after instance closure
	 * 
	 * @throws Exception
	 */
	public void clear() throws Exception {
		logger.info("Class instance was cleaned!");
		deleteCopiedFiles(new File(temporaryFolderPath));
	}

	/**
	 * Copies the passed filenames to a temporary path to use them in the tests
	 * 
	 * @throws Exception
	 */
	private void createNewTestCaseDirectory(String[] classNames) throws Exception {
		// before copying files to the test path, check if all files are in the resource
		// directory
		for (String className : classNames) {
			if (!Arrays.asList(resourceNames).contains(className)) {
				throw new FileNotFoundException(
						String.format("The specified class could not be found! [%s]", className));
			}
		}
		// Copy the resources data to the temporary path
		for (int counter = 0; counter < classNames.length; counter++) {
			Path originalPath = Path.of(Constant.BASE_PATH_TO_JAVA_RESOURCES_SORTALGO.toString(), classNames[counter]);
			Path copiePath = Path.of(temporaryFolderPath, Constant.TEMP_DIRECTORY_NAME + (counter + 1),
					classNames[counter]);
			try {
				File directory = new File(copiePath.toString());
				if (!directory.exists()) {
					directory.mkdirs();
				}

				logger.info(String.format("Copy file from [%s] to [%s]", originalPath, copiePath));
				Files.copy(originalPath, copiePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ioException) {
				logger.error(String.format("The specified file could not be copied! [%s] \n Exception [%s] ",
						classNames[counter], ioException.getMessage()));
				throw ioException;
			}
		}
	}

	/**
	 * Delete directory with including files
	 * 
	 * @param file
	 */
	private void deleteCopiedFiles(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteCopiedFiles(f);
				} else {
					logger.info(String.format("Delete file in folder: [%s]", f.toString()));
					f.delete();
				}
			}
		}
		logger.info(String.format("Delete folder: [%s]", folder.toString()));
		folder.delete();
	}

}
