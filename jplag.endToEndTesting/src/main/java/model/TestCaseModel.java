package model;

import java.util.ArrayList;
import java.util.Arrays;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;

public class TestCaseModel {
	private ResultJsonModel resultJsonModel;
	private LanguageOption languageOption;
	private String submissionFolderPath;

	public TestCaseModel(String submissionFolderPath, ResultJsonModel resultJsonModel, LanguageOption languageOption) {
		this.submissionFolderPath = submissionFolderPath;
		this.resultJsonModel = resultJsonModel;
		this.languageOption = languageOption;
	}

	public Boolean compaireModelProperties(JPlagResult jplagResult) {
		// for the fine-granular testing strategy, the result object contains only one
		// comparison.
		for (JPlagComparison jPlagComparison : jplagResult.getAllComparisons()) {
			System.out.println("Comparison of the stored values and the current equality values");
			if (Float.compare(jPlagComparison.similarity(),  resultJsonModel.similarity()) != 0) {
				return false;
			}
		}
		return true;
	}

	public JPlagOptions getJPlagOptionsFromCurrentModel() {
		return new JPlagOptions(new ArrayList<>(Arrays.asList(submissionFolderPath)), new ArrayList<String>(),
				languageOption);
	}
}
