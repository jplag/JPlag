package de.jplag.end_to_end_testing.model;

import java.util.ArrayList;
import java.util.Arrays;

import de.jplag.options.JPlagOptions;

/**
 * Median class for trading the results and the paths used in the tests. Also the JPlagOptions used for a test run are
 * created here.
 */
public class TestCaseModel {

    private JsonModel jsonModel;
    private String languageIdentifier;
    private String submissionFolderPath;

    /**
     * Constructor for creating the instance
     * @param submissionFolderPath path to the plagiarism folder structure to be tested using Jplag
     * @param jsonModel the parsed result model needed for the test to be able to match it later in the tests.
     * @param languageIdentifier the language selection in which the tests take place
     */
    public TestCaseModel(String submissionFolderPath, JsonModel jsonModel, String languageIdentifier) {
        this.submissionFolderPath = submissionFolderPath;
        this.jsonModel = jsonModel;
        this.languageIdentifier = languageIdentifier;
    }

    /**
     * returns the, for the test, current result model that has been persisted in the saved list.
     * @return ResultJsonModel loaded for the test
     */
    public JsonModel getCurrentJsonModel() {
        return jsonModel;
    }

    /**
     * creates the JPlag options for the test run from the values passed in the constructor.
     * @return current JPlag options for the created object
     */
    public JPlagOptions getJPlagOptionsFromCurrentModel() {
        return new JPlagOptions(new ArrayList<>(Arrays.asList(submissionFolderPath)), new ArrayList<>(), languageIdentifier);
    }
}
