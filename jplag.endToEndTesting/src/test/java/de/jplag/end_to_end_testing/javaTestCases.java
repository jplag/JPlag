package de.jplag.end_to_end_testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.end_to_end_testing.constants.Constant;
import de.jplag.end_to_end_testing.helper.JPlagTestSuiteHelper;
import de.jplag.end_to_end_testing.model.TestCaseModel;
import de.jplag.exceptions.ExitException;
import de.jplag.options.LanguageOption;

/**
 * Main test class for end-to-end testing in the Java language. The test cases aim to detect changes in the detection of
 * plagiarism in the Java language and to be able to roughly categorize them. The plagiarism is compared with the
 * original class. The results are compared with the results from previous tests and changes are detected.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class javaTestCases {
    private static final Logger logger = LoggerFactory.getLogger(JPlagTestSuiteHelper.class);

    private JPlagTestSuiteHelper jplagTestSuiteHelper;

    @BeforeAll
    public void setUp() throws IOException {
        jplagTestSuiteHelper = new JPlagTestSuiteHelper(LanguageOption.JAVA);
        assertTrue(Constant.BASE_PATH_TO_JAVA_RESOURCES_SORTALGO.toFile().exists(), "Could not find base directory!");
        assertTrue(Constant.BASE_PATH_TO_JAVA_RESULT_JSON.toFile().exists(),
                "Could not find java result json at " + Constant.BASE_PATH_TO_JAVA_RESULT_JSON + "!");
    }

    @AfterEach
    public void teardown() {
        // after close the created directories are deleted
        jplagTestSuiteHelper.clear();
    }

    /**
     * Inserting comments or empty lines (normalization level)
     * @throws IOException is thrown in case of problems with copying the plagiarism classes
     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
     */
    @Test
    void normalizationLevelTestOne() throws IOException, ExitException {

        String[] testClassNames = new String[] {"SortAlgo.java", "SortAlgo1.java"};

        TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

        JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

        var resultJsonModel = testCaseModel.getCurrentResultJsonModel();

        for (JPlagComparison jPlagComparison : jplagResult.getAllComparisons()) {
            logger.info("Comparison of the stored values and the current equality values");
            assertEquals(resultJsonModel.similarity(), jPlagComparison.similarity(),
                    "The JPlag results [similarity] do not match the stored values!");
        }
    }

    /**
     * Changing variable names or function names (normalization level)
     * @throws IOException is thrown in case of problems with copying the plagiarism classes
     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
     */
    @Test
    void normalizationLevelTestTwo() throws IOException, ExitException {
        String[] testClassNames = new String[] {"SortAlgo.java", "SortAlgo2.java"};

        TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

        JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

        var resultJsonModel = testCaseModel.getCurrentResultJsonModel();

        for (JPlagComparison jPlagComparison : jplagResult.getAllComparisons()) {
            logger.info("Comparison of the stored values and the current equality values");
            assertEquals(resultJsonModel.similarity(), jPlagComparison.similarity(),
                    "The JPlag results [similarity] do not match the stored values!");
        }
    }

    /**
     * Inserting comments or empty lines (normalization level)
     * @throws IOException is thrown in case of problems with copying the plagiarism classes
     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
     */
    @Test
    void normalizationLevelTestThree() throws IOException, ExitException {
        String[] testClassNames = new String[] {"SortAlgo1.java", "SortAlgo2.java"};

        TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

        JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

        var resultJsonModel = testCaseModel.getCurrentResultJsonModel();

        for (JPlagComparison jPlagComparison : jplagResult.getAllComparisons()) {
            logger.info("Comparison of the stored values and the current equality values");
            assertEquals(resultJsonModel.similarity(), jPlagComparison.similarity(),
                    "The JPlag results [similarity] do not match the stored values!");
        }
    }

    /**
     * Insertion of unnecessary or changed code lines (token generation)
     * @throws IOException is thrown in case of problems with copying the plagiarism classes
     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
     */
    @Test
    void tokenGenerationLevelTestOne() throws IOException, ExitException {
        String[] testClassNames = new String[] {"SortAlgo.java", "SortAlgo3.java"};

        TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

        JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

        var resultJsonModel = testCaseModel.getCurrentResultJsonModel();

        for (JPlagComparison jPlagComparison : jplagResult.getAllComparisons()) {
            logger.info("Comparison of the stored values and the current equality values");
            assertEquals(resultJsonModel.similarity(), jPlagComparison.similarity(),
                    "The JPlag results [similarity] do not match the stored values!");
        }
    }

    /**
     * Changing the program flow (token generation) (statements and functions must be independent from each other)
     * @throws IOException is thrown in case of problems with copying the plagiarism classes
     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
     */
    @Test
    void tokenGenerationLevelTestTwo() throws IOException, ExitException {
        String[] testClassNames = new String[] {"SortAlgo.java", "SortAlgo4.java"};

        TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

        JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

        var resultJsonModel = testCaseModel.getCurrentResultJsonModel();

        for (JPlagComparison jPlagComparison : jplagResult.getAllComparisons()) {
            logger.info("Comparison of the stored values and the current equality values");
            assertEquals(resultJsonModel.similarity(), jPlagComparison.similarity(),
                    "The JPlag results [similarity] do not match the stored values!");
        }
    }

    /**
     * Variable declaration at the beginning of the program (Detecting Source Code Plagiarism [...])
     * @throws IOException is thrown in case of problems with copying the plagiarism classes
     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
     */
    @Test
    void tokenGenerationLevelTestThree() throws IOException, ExitException {
        String[] testClassNames = new String[] {"SortAlgo.java", "SortAlgo4d1.java"};

        TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

        JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

        var resultJsonModel = testCaseModel.getCurrentResultJsonModel();

        for (JPlagComparison jPlagComparison : jplagResult.getAllComparisons()) {
            logger.info("Comparison of the stored values and the current equality values");
            assertEquals(resultJsonModel.similarity(), jPlagComparison.similarity(),
                    "The JPlag results [similarity] do not match the stored values!");
        }
    }
}
