package de.jplag.endtoend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.cli.LanguageLoader;
import de.jplag.endtoend.constants.TestDirectoryConstants;
import de.jplag.endtoend.helper.FileHelper;
import de.jplag.endtoend.helper.TestSuiteHelper;
import de.jplag.endtoend.model.ExpectedResult;
import de.jplag.endtoend.model.ResultDescription;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Main test class for end-to-end testing in all language. The test cases aim to detect changes in the detection of
 * plagiarism in the Java language and to be able to roughly categorize them. The plagiarism is compared with the
 * original class. The results are compared with the results from previous tests and changes are detected.
 */
class EndToEndSuiteTest {
    private static final double EPSILON = 1E-8;

    /**
     * Creates the test cases over all language options for which data is available and the current test options.
     * @return dynamic test cases across all test data and languages
     * @throws IOException is thrown for all problems that may occur while parsing the json file.
     */
    @TestFactory
    Collection<DynamicContainer> dynamicOverAllTest() throws IOException, ExitException {
        File resultsDirectory = TestDirectoryConstants.BASE_PATH_TO_RESULT_JSON.toFile();
        File[] languageDirectories = resultsDirectory.listFiles(File::isDirectory);
        List<DynamicContainer> allTests = new LinkedList<>();
        for (File languageDirectory : languageDirectories) {
            Language language = LanguageLoader.getLanguage(languageDirectory.getName()).orElseThrow();
            File[] resultJsons = languageDirectory.listFiles(file -> !file.isDirectory() && file.getName().endsWith(".json"));
            List<DynamicContainer> languageTests = new LinkedList<>();
            for (File resultJson : resultJsons) {
                List<DynamicContainer> testContainers = new LinkedList<>();
                ResultDescription[] results = new ObjectMapper().readValue(resultJson, ResultDescription[].class);
                for (var result : results) {
                    var testCases = generateTestsForResultDescription(resultJson, result, language);
                    testContainers.add(DynamicContainer.dynamicContainer("MTM: " + result.options().minimumTokenMatch(), testCases));
                }
                languageTests.add(DynamicContainer.dynamicContainer(FileHelper.getFileNameWithoutFileExtension(resultJson), testContainers));
            }
            allTests.add(DynamicContainer.dynamicContainer(language.getIdentifier(), languageTests));
        }
        return allTests;
    }

    /**
     * Generates test cases for each test described in the provided result object.
     * @param resultJson is the file of the result json
     * @param result is one test suite configuration of the deserialized {@code resultJson}
     * @param language is the language to run JPlag with
     * @return a collection of test cases, each validating one {@link JPlagResult} against its {@link ExpectedResult}
     * counterpart
     */
    private Collection<DynamicTest> generateTestsForResultDescription(File resultJson, ResultDescription result, Language language)
            throws ExitException {
        File submissionDirectory = TestSuiteHelper.getSubmissionDirectory(language, resultJson);
        JPlagOptions jplagOptions = new JPlagOptions(language, Set.of(submissionDirectory), Set.of())
                .withMinimumTokenMatch(result.options().minimumTokenMatch());
        JPlagResult jplagResult = new JPlag(jplagOptions).run();
        Map<String, JPlagComparison> jPlagComparisons = jplagResult.getAllComparisons().stream()
                .collect(Collectors.toMap(it -> TestSuiteHelper.getTestIdentifier(it), it -> it));
        assertEquals(result.identifierToResultMap().size(), jPlagComparisons.size(), "different number of results and expected results");

        return result.identifierToResultMap().keySet().stream().map(identifier -> {
            JPlagComparison comparison = jPlagComparisons.get(identifier);
            ExpectedResult expectedResult = result.identifierToResultMap().get(identifier);
            return generateTest(identifier, expectedResult, comparison);
        }).toList();
    }

    /**
     * Generates a test case validating the passed result by comparing it to the expected result values.
     * @param name is the name of the test case.
     * @param expectedResult contains all expected result values.
     * @param result is the comparison object generated from running JPlag.
     */
    private DynamicTest generateTest(String name, ExpectedResult expectedResult, JPlagComparison result) {
        return DynamicTest.dynamicTest(name, () -> {
            assertNotNull(result, "No comparison result could be found");

            List<String> validationErrors = new ArrayList<>();
            if (areDoublesDifferent(expectedResult.resultSimilarityMinimum(), result.minimalSimilarity())) {
                validationErrors.add(formattedValidationError("minimal similarity", String.valueOf(expectedResult.resultSimilarityMinimum()),
                        String.valueOf(result.minimalSimilarity())));
            }
            if (areDoublesDifferent(expectedResult.resultSimilarityMaximum(), result.maximalSimilarity())) {
                validationErrors.add(formattedValidationError("maximal similarity", String.valueOf(expectedResult.resultSimilarityMaximum()),
                        String.valueOf(result.maximalSimilarity())));
            }
            if (expectedResult.resultMatchedTokenNumber() != result.getNumberOfMatchedTokens()) {
                validationErrors.add(formattedValidationError("number of matched tokens", String.valueOf(expectedResult.resultMatchedTokenNumber()),
                        String.valueOf(result.getNumberOfMatchedTokens())));
            }

            assertTrue(validationErrors.isEmpty(), createValidationErrorOutput(validationErrors));
        });
    }

    private boolean areDoublesDifferent(double d1, double d2) {
        return Math.abs(d1 - d2) >= EPSILON;
    }

    /**
     * Creates the display message for a result value validation error.
     * @param valueName Name of the failed test object
     * @param actualValue actual test value
     * @param expectedValue expected test value
     */
    private String formattedValidationError(String valueName, String actualValue, String expectedValue) {
        return valueName + " was " + actualValue + " but expected " + expectedValue;
    }

    /**
     * Creates the display info from the passed failed test results
     * @return formatted text for the failed comparative values of the current test
     */
    private String createValidationErrorOutput(List<String> validationErrors) {
        return "There were " + validationErrors.size() + " validation error(s):" + System.lineSeparator()
                + String.join(System.lineSeparator(), validationErrors);
    }
}
