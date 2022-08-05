package de.jplag.end_to_end_testing.model;

import java.util.ArrayList;
import java.util.Arrays;

import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;

/**
 * Median class for trading the results and the paths used in the tests. Also the JPlagOptions used for a test run are
 * created here.
 */
public class TestCaseModel {

    private ResultJsonModel resultJsonModel;
    private LanguageOption languageOption;
    private String submissionFolderPath;

    /**
     * Constructor for creating the instance
     * @param submissionFolderPath path to the plagiarism folder structure to be tested using Jplag
     * @param resultJsonModel the parsed result model needed for the test to be able to match it later in the tests.
     * @param languageOption the language selection in which the tests take place
     */
    public TestCaseModel(String submissionFolderPath, ResultJsonModel resultJsonModel, LanguageOption languageOption) {
        this.submissionFolderPath = submissionFolderPath;
        this.resultJsonModel = resultJsonModel;
        this.languageOption = languageOption;
    }

    /**
     * returns the, for the test, current result model that has been persisted in the saved list.
     * @return ResultJsonModel loaded for the test
     */
    public ResultJsonModel getCurrentResultJsonModel() {
        return resultJsonModel;
    }

    /**
     * creates the JPlag options for the test run from the values passed in the constructor.
     * @return current JPlag options for the created object
     */
    public JPlagOptions getJPlagOptionsFromCurrentModel() {
        return new JPlagOptions(new ArrayList<String>(Arrays.asList(submissionFolderPath)), new ArrayList<String>(), languageOption);
    }
}
