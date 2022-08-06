package de.jplag.end_to_end_testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
class JavaEndToEndTest {

    private JPlagTestSuiteHelper jplagTestSuiteHelper;

    @BeforeAll
    public void setUp() throws IOException {
        jplagTestSuiteHelper = new JPlagTestSuiteHelper(LanguageOption.JAVA);
        assertTrue(Constant.BASE_PATH_TO_JAVA_RESOURCES_SORTALGO.toFile().exists(), "Could not find base directory!");
        assertTrue(Constant.BASE_PATH_TO_JAVA_RESULT_JSON.toFile().isFile(), "Could not find java result json!");
    }

    @AfterEach
    public void teardown() {
        // after close the created directories are deleted
        jplagTestSuiteHelper.clear();
    }

    /**
     * Test cases created for the normalization level
     * @return the classes to be tested with the corresponding identifier
     */
    private static Stream<Arguments> normalizationLevelTestArguments() {
        return Stream.of(Arguments.of((Object) new String[] {"SortAlgo.java", "SortAlgo1.java"}, 0),
                Arguments.of((Object) new String[] {"SortAlgo.java", "SortAlgo2.java"}, 1),
                Arguments.of((Object) new String[] {"SortAlgo1.java", "SortAlgo2.java"}, 2));
    }

    /**
     * Test cases for the token generation level
     * @return the classes to be tested with the corresponding identifier
     */
    private static Stream<Arguments> tokenGenerationLevelTestArguments() {
        return Stream.of(Arguments.of((Object) new String[] {"SortAlgo.java", "SortAlgo3.java"}, 0),
                Arguments.of((Object) new String[] {"SortAlgo.java", "SortAlgo4.java"}, 1),
                Arguments.of((Object) new String[] {"SortAlgo.java", "SortAlgo4d1.java"}, 2));
    }

    /**
     * Inserting comments or empty lines (normalization level) -> id = 0 Changing variable names or function names
     * (normalization level) -> id = 1 Inserting comments or empty lines (normalization level) -> id = 2
     * @param testClassNames Plagiarized classes names in the resource directorie which are needed for the test
     * @param testId name of the testId to load and identify the stored results
     * @throws IOException is thrown in case of problems with copying the plagiarism classes
     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
     */
    @ParameterizedTest
    @MethodSource("normalizationLevelTestArguments")
    void normalizationLevelTest(String[] testClassNames, int testId) throws IOException, ExitException {

        TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

        JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

        var jsonModel = testCaseModel.getCurrentJsonModel();

        for (JPlagComparison jPlagComparison : jplagResult.getAllComparisons()) {
            assertEquals(jsonModel.getResultModelById(testId).getResultSimilarity(), jPlagComparison.similarity(),
                    "The JPlag results [similarity] do not match the stored values!");
        }
    }

    /**
     * Insertion of unnecessary or changed code lines (token generation) -> id = 0 Changing the program flow (token
     * generation) (statements and functions must be independent from each other) -> id = 1 Variable declaration at the
     * beginning of the program (Detecting Source Code Plagiarism [...]) -> id = 2
     * @param testClassNames Plagiarized classes names in the resource directorie which are needed for the test
     * @param testId name of the testId to load and identify the stored results
     * @throws IOException is thrown in case of problems with copying the plagiarism classes
     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
     */
    @ParameterizedTest
    @MethodSource("tokenGenerationLevelTestArguments")
    void tokenGenerationLevelTest(String[] testClassNames, int testId) throws IOException, ExitException {

        TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

        JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

        var jsonModel = testCaseModel.getCurrentJsonModel();

        for (JPlagComparison jPlagComparison : jplagResult.getAllComparisons()) {
            assertEquals(jsonModel.getResultModelById(testId).getResultSimilarity(), jPlagComparison.similarity(),
                    "The JPlag results [similarity] do not match the stored values!");
        }
    }
}
