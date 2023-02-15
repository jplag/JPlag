package de.jplag.endtoend;

import static de.jplag.options.SimilarityMetric.INTERSECTION;
import static de.jplag.options.SimilarityMetric.MAX;
import static de.jplag.options.SimilarityMetric.MIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;
import java.util.List;
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
import de.jplag.endtoend.helper.DeltaSummaryStatistics;
import de.jplag.endtoend.helper.FileHelper;
import de.jplag.endtoend.helper.TestSuiteHelper;
import de.jplag.endtoend.model.ExpectedResult;
import de.jplag.endtoend.model.ResultDescription;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.options.SimilarityMetric;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Main test suite for end-to-end testing over all languages. The test suite aims to detect changes regarding the
 * detection quality of JPlag. Artificial plagiarisms are compared with the original code. The results are compared with
 * previous ones stored in the resource folder.
 */
class EndToEndSuiteTest {
    private static final double EPSILON = 1E-8;

    /**
     * Creates the test cases over all language options for which data is available and the current test options.
     * @return dynamic test cases across all test data and languages
     * @throws IOException is thrown for all problems that may occur while parsing the json file.
     */
    @TestFactory
    Collection<DynamicContainer> endToEndTestFactory() throws ExitException {
        File resultDirectory = TestDirectoryConstants.BASE_PATH_TO_RESULT_JSON.toFile();
        List<File> languageDirectories = Arrays.asList(resultDirectory.listFiles(File::isDirectory));
        List<DynamicContainer> allTests = new ArrayList<>();
        for (File languageDirectory : languageDirectories) {
            allTests.add(generateTestForLanguage(languageDirectory));
        }
        return allTests;
    }

    private DynamicContainer generateTestForLanguage(File languageDirectory) throws ExitException {
        Language language = LanguageLoader.getLanguage(languageDirectory.getName()).orElseThrow();
        File[] resultJsons = languageDirectory.listFiles(file -> !file.isDirectory() && file.getName().endsWith(".json"));
        List<DynamicContainer> languageTests = new LinkedList<>();
        for (File resultJson : resultJsons) { // for each data set
            languageTests.add(generateTestsForDataSet(language, resultJson));
        }
        return DynamicContainer.dynamicContainer(language.getIdentifier(), languageTests);
    }

    private DynamicContainer generateTestsForDataSet(Language language, File resultJson) throws ExitException {
        List<DynamicContainer> testContainers = new LinkedList<>();
        ResultDescription[] results;
        try {
            results = new ObjectMapper().readValue(resultJson, ResultDescription[].class);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load expected values.", exception);
        }
        for (var result : results) { // for each configuration
            var testCases = generateTestsForResultDescription(resultJson, result, language);
            testContainers.add(DynamicContainer.dynamicContainer("MTM: " + result.options().minimumTokenMatch(), testCases));
        }
        return DynamicContainer.dynamicContainer(FileHelper.getFileNameWithoutFileExtension(resultJson), testContainers);
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
        var comparisons = jplagResult.getAllComparisons().stream().collect(Collectors.toMap(it -> TestSuiteHelper.getTestIdentifier(it), it -> it));
        assertEquals(result.identifierToResultMap().size(), comparisons.size(), "different number of results and expected results");

        DeltaSummaryStatistics statistics = new DeltaSummaryStatistics();
        var tests = new ArrayList<>(result.identifierToResultMap().keySet().stream().map(identifier -> {
            JPlagComparison comparison = comparisons.get(identifier);
            ExpectedResult expectedResult = result.identifierToResultMap().get(identifier);
            return generateTest(identifier, expectedResult, comparison, statistics);
        }).toList());
        tests.addAll(evaluateDeviationOfSimilarity(statistics));
        return tests;
    }

    /**
     * Generates a test case validating the passed result by comparing it to the expected result values.
     * @param name is the name of the test case.
     * @param expectedResult contains all expected result values.
     * @param result is the comparison object generated from running JPlag.
     */
    private DynamicTest generateTest(String name, ExpectedResult expectedResult, JPlagComparison result, DeltaSummaryStatistics statistics) {
        return DynamicTest.dynamicTest(name, () -> {
            assertNotNull(result, "No comparison result could be found");
            List<String> errors = new ArrayList<>();
            for (SimilarityMetric metric : List.of(MIN, MAX)) {
                double expected = expectedResult.getSimilarityForMetric(metric);
                double actual = metric.applyAsDouble(result);
                if (Math.abs(expected - actual) >= EPSILON) {
                    errors.add(formattedValidationError(metric, expected, actual));
                }
                statistics.accept(actual, expected);
            }
            if (expectedResult.resultMatchedTokenNumber() != result.getNumberOfMatchedTokens()) {
                errors.add(formattedValidationError(INTERSECTION, expectedResult.resultMatchedTokenNumber(), result.getNumberOfMatchedTokens()));
            }
            assertTrue(errors.isEmpty(), createValidationErrorOutput(name, errors));
        });
    }

    /**
     * Creates the display message for a result value validation error.
     * @param valueName Name of the failed test object
     * @param actualValue actual test value
     * @param expectedValue expected test value
     */
    private String formattedValidationError(SimilarityMetric metric, Number actualValue, Number expectedValue) {
        return metric + " was " + String.valueOf(actualValue) + " but expected " + String.valueOf(expectedValue);
    }

    /**
     * Creates the display info from the passed failed test results
     * @return formatted text for the failed comparative values of the current test
     */
    private String createValidationErrorOutput(String name, List<String> validationErrors) {
        return name + ": There were " + validationErrors.size() + " validation error(s):" + System.lineSeparator()
                + String.join(System.lineSeparator(), validationErrors);
    }

    private List<DynamicTest> evaluateDeviationOfSimilarity(DeltaSummaryStatistics deltaStatistics) {
        return List.of(deviationOfSimilarityTest("positive", deltaStatistics.getPositiveStatistics()),
                deviationOfSimilarityTest("negative", deltaStatistics.getNegativeStatistics()));
    }

    private DynamicTest deviationOfSimilarityTest(String textualSign, DoubleSummaryStatistics statistics) {
        return DynamicTest.dynamicTest("OVERVIEW: " + textualSign + " similarity deviation", () -> {
            if (Math.abs(statistics.getAverage()) > EPSILON) {
                fail(textualSign + " deviation over all AVG similarity values:" + System.lineSeparator() + statistics.toString());
            }
        });
    }
}
