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
	 * compares the current result with the values stored in the object
	 * @param jplagResult
	 * @return true if the values match otherwise false
	 */
	public Boolean compaireModelProperties(JPlagResult jplagResult) {
		// for the fine granular testing strategy, the result object contains only one
		// comparison.
		for (JPlagComparison jPlagComparison : jplagResult.getAllComparisons()) {
			logger.info("Comparison of the stored values and the current equality values");
			if (Float.compare(jPlagComparison.similarity(), resultJsonModel.similarity()) != 0) {
				return false;
			}
		}
		return true;
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
