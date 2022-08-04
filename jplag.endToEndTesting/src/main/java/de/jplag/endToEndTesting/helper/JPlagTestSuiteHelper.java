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

		this.resultModel = JsonHelper
				.getResultModelFromPath(Constant.BASE_PATH_TO_JAVA_RESULT_JSON.toAbsolutePath().toString());
		logger.info(String.format("temp path at [%s]", Constant.TEMPORARY_SUBMISSION_DIRECTORY_NAME));
	}

	/**
	 * creates all necessary folder paths and objects for a test run. Also searches
	 * for the stored previous results of the test in order to compare them with the
	 * current results.
	 * 
	 * @param classNames
	 * @return comparison results saved for the test
	 * @throws Exception
	 */
	public TestCaseModel createNewTestCase(String[] classNames) throws Exception {
		createNewTestCaseDirectory(classNames);
		var functionName = StackWalker.getInstance().walk(stream -> stream.skip(1).findFirst().get()).getMethodName();
		ResultJsonModel resultJsonModel = resultModel.stream()
				.filter(jsonModel -> functionName.equals(jsonModel.getFunctionName())).findAny().orElse(null);
		return new TestCaseModel(Constant.TEMPORARY_SUBMISSION_DIRECTORY_NAME, resultJsonModel, languageOption);
	}

	/**
	 * The copied data should be deleted after instance closure
	 * 
	 * @throws Exception
	 */
	public void clear() throws Exception {
		logger.info("Class instance was cleaned!");
		deleteCopiedFiles(new File(Constant.TEMPORARY_SUBMISSION_DIRECTORY_NAME));
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
			Path copiePath = Path.of(Constant.TEMPORARY_SUBMISSION_DIRECTORY_NAME, Constant.TEMPORARY_DIRECTORY_NAME + (counter + 1),
					classNames[counter]);

			File directory = new File(copiePath.toString());
			if (!directory.exists()) {
				directory.mkdirs();
			}

			Files.copy(originalPath, copiePath, StandardCopyOption.REPLACE_EXISTING);
			logger.info(String.format("Copy file from [%s] to [%s]", originalPath, copiePath));
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
