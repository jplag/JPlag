package model;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;

public class TestCaseModel {
	private static final Logger logger = LoggerFactory.getLogger("EndToEndTesting");
	
	private ResultJsonModel resultJsonModel;
	private LanguageOption languageOption;
	private String submissionFolderPath;

	public TestCaseModel(String submissionFolderPath, ResultJsonModel resultJsonModel, LanguageOption languageOption) {
		this.submissionFolderPath = submissionFolderPath;
		this.resultJsonModel = resultJsonModel;
		this.languageOption = languageOption;
	}
	
	/**
	 * returns the, for the test, current result model that has been persisted in the saved list. 
	 * @return ResultJsonModel loaded for the test
	 */
	public ResultJsonModel getCurrentResultJsonModel()
	{
		return resultJsonModel;
	}
	
	/**
	 * creates the JPlag options for the test run from the values passed in the constructor.
	 * @return current JPlag options for the created object
	 */
	public JPlagOptions getJPlagOptionsFromCurrentModel() {
		return new JPlagOptions(new ArrayList<>(Arrays.asList(submissionFolderPath)), new ArrayList<String>(),
				languageOption);
	}
}
