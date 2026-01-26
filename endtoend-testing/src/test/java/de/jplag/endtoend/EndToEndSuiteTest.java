package de.jplag.endtoend;

import static de.jplag.options.SimilarityMetric.MAX;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.Submission;
import de.jplag.endtoend.constants.TestDirectoryConstants;
import de.jplag.endtoend.helper.DeltaSummaryStatistics;
import de.jplag.endtoend.helper.FileHelper;
import de.jplag.endtoend.helper.TestSuiteHelper;
import de.jplag.endtoend.model.ComparisonIdentifier;
import de.jplag.endtoend.model.DataSet;
import de.jplag.endtoend.model.DataSetRunConfiguration;
import de.jplag.endtoend.model.ExpectedResult;
import de.jplag.endtoend.model.GoldStandard;
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
    private static final double EPSILON = 1E-6;

    /**
     * Creates the test cases over all language options for which data is available and the current test options.
     * @return dynamic test cases across all test data and languages
     * @throws ExitException If JPlag throws an error
     * @throws IOException If loading test resources fails
     */
    @TestFactory
    Collection<DynamicContainer> endToEndTestFactory() throws ExitException, IOException {
        File descriptorDirectory = TestDirectoryConstants.BASE_PATH_TO_DATA_SET_DESCRIPTORS.toFile();
        List<File> testDescriptorFiles = Arrays.asList(Objects.requireNonNull(descriptorDirectory.listFiles()));
        List<DynamicContainer> allTests = new ArrayList<>();

        Map<Language, List<DataSet>> dataSetsByLanguage = testDescriptorFiles.stream().map(testDescriptorFile -> {
            try {
                return new ObjectMapper().readValue(testDescriptorFile, DataSet.class);
            } catch (IOException e) {
                throw new IllegalStateException("The test descriptor " + testDescriptorFile.getName() + " is invalid.");
            }
        }).collect(Collectors.groupingBy(DataSet::language));

        for (Language language : dataSetsByLanguage.keySet()) {
            allTests.add(generateTestForLanguage(language, dataSetsByLanguage.get(language)));
        }

        return allTests;
    }

    /**
     * Generates the tests for the given language.
     * @param language The language
     * @param dataSets The data sets for this language
     * @return The dynamic container containing the tests
     * @throws ExitException If JPlag throws an error
     */
    private DynamicContainer generateTestForLanguage(Language language, List<DataSet> dataSets) throws ExitException, IOException {
        List<DynamicContainer> languageTests = new LinkedList<>();
        for (DataSet dataSet : dataSets) {
            languageTests.add(generateTestsForDataSet(dataSet));
        }
        return DynamicContainer.dynamicContainer(language.getIdentifier(), languageTests);
    }

    /**
     * Generates tests for a data set.
     * @param dataSet The data set
     * @return The dynamic container containing the tests
     * @throws ExitException If JPlag throws an error
     */
    private DynamicContainer generateTestsForDataSet(DataSet dataSet) throws ExitException, IOException {
        List<DynamicContainer> testContainers = new LinkedList<>();
        Map<String, ResultDescription> results = new HashMap<>();
        try {
            ResultDescription[] resultList = new ObjectMapper().readValue(dataSet.getResultFile(), ResultDescription[].class);
            for (ResultDescription resultDescription : resultList) {
                results.put(resultDescription.identifier(), resultDescription);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load expected values.", exception);
        }

        for (DataSetRunConfiguration runConfiguration : DataSetRunConfiguration.generateRunConfigurations(dataSet)) {
            if (!results.containsKey(runConfiguration.identifier())) {
                throw new IllegalStateException("Expected results don't match data set configuration");
            }
            testContainers.add(generateTestsForResultDescription(results.get(runConfiguration.identifier()), dataSet, runConfiguration));
        }

        return DynamicContainer.dynamicContainer(FileHelper.getFileNameWithoutFileExtension(dataSet.getResultFile()), testContainers);
    }

    /**
     * Generates test cases for each test described in the provided result object.
     * @param result is one test suite configuration of the deserialized {@code resultJson}
     * @param dataSet The data set, the test is for
     * @param runConfiguration The run configuration for the test
     * @return a collection of test cases, each validating one {@link JPlagResult} against its {@link ExpectedResult}
     * counterpart
     * @throws ExitException If JPlag throw an error
     */
    private DynamicContainer generateTestsForResultDescription(ResultDescription result, DataSet dataSet, DataSetRunConfiguration runConfiguration)
            throws ExitException, IOException {
        JPlagOptions options = runConfiguration.jPlagOptions();
        JPlagResult jplagResult = JPlag.run(options);
        var comparisons = jplagResult.getAllComparisons().stream().collect(Collectors.toMap(TestSuiteHelper::getTestIdentifier, it -> it));
        assertEquals(result.identifierToResultMap().size(), comparisons.size(), "different number of results and expected results");

        DynamicContainer comparisonTests = generateTestResultsForComparisons(result, comparisons);
        DynamicNode detectionTest = generateGoldStandardTest(dataSet, comparisons, result.goldStandard());

        return DynamicContainer.dynamicContainer(runConfiguration.identifier(), List.of(comparisonTests, detectionTest));
    }

    /**
     * Generates the test cases for the individual comparisons.
     * @param result The result description for the tests
     * @param comparisons The comparisons
     * @return The container with the tests
     */
    private DynamicContainer generateTestResultsForComparisons(ResultDescription result, Map<String, JPlagComparison> comparisons) {
        DeltaSummaryStatistics statistics = new DeltaSummaryStatistics();
        var tests = new ArrayList<>(result.identifierToResultMap().keySet().stream().map(identifier -> {
            JPlagComparison comparison = comparisons.get(identifier);
            ExpectedResult expectedResult = result.identifierToResultMap().get(identifier);
            return generateTest(identifier, expectedResult, comparison, statistics);
        }).toList());
        tests.addAll(evaluateDeviationOfSimilarity(statistics));

        return DynamicContainer.dynamicContainer("comparison changes", tests);
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
            double expected = expectedResult.getSimilarityForMetric(MAX);
            double actual = MAX.applyAsDouble(result);
            if (Math.abs(expected - actual) >= EPSILON) {
                errors.add(formattedValidationError(MAX, expected, actual));
            }
            statistics.accept(actual, expected);
            assertTrue(errors.isEmpty(), createValidationErrorOutput(name, errors, result));
        });
    }

    /**
     * Generates the tests for the gold standard.
     * @param dataSet The data set
     * @param comparisonMap The comparisons
     * @param goldStandard The gold standard previously saved
     * @return The node containing the tests
     */
    private DynamicNode generateGoldStandardTest(DataSet dataSet, Map<String, JPlagComparison> comparisonMap, GoldStandard goldStandard)
            throws IOException {
        if (goldStandard != null) {
            Set<ComparisonIdentifier> goldStandardIdentifiers = ComparisonIdentifier
                    .loadIdentifiersFromFile(dataSet.getGoldStandardFile().orElseThrow(), dataSet.getActualDelimiter());
            GoldStandard found = GoldStandard.buildFromComparisons(comparisonMap.values(), goldStandardIdentifiers);

            DynamicTest goldStandardMatch = DynamicTest.dynamicTest("expected plagiarism comparisons average similarity",
                    () -> assertEquals(goldStandard.matchAverage(), found.matchAverage(), EPSILON,
                            "expected plagiarism comparisons have deviating similarities"));

            DynamicTest goldStandardNonMatch = DynamicTest.dynamicTest("expected non plagiarism comparisons average",
                    () -> assertEquals(goldStandard.nonMatchAverage(), found.nonMatchAverage(), EPSILON,
                            "expected non plagiarism comparisons have deviating similarities"));

            return DynamicContainer.dynamicContainer("expected plagiarism test", List.of(goldStandardMatch, goldStandardNonMatch));
        }
        return DynamicTest.dynamicTest("expected plagiarisms skipped",
                () -> Assumptions.abort("The expected plagiarisms test is skipped, because no expected plagiarisms are defined."));
    }

    /**
     * Creates the display message for a result value validation error.
     * @param metric The metric the test failed for
     * @param actualValue actual test value
     * @param expectedValue expected test value
     */
    private String formattedValidationError(SimilarityMetric metric, Number actualValue, Number expectedValue) {
        return metric + " was " + actualValue + " but expected " + expectedValue;
    }

    /**
     * Creates the display info from the passed failed test results.
     * @return formatted text for the failed comparative values of the current test
     */
    private String createValidationErrorOutput(String name, List<String> validationErrors, JPlagComparison result) {
        return name + ": There were " + validationErrors.size() + " validation error(s):" + System.lineSeparator()
                + String.join(System.lineSeparator(), validationErrors) + System.lineSeparator() + "First  file tokens: "
                + String.join(",", getTokenNames(result.firstSubmission())) + System.lineSeparator() + "Second file tokens: "
                + String.join(",", getTokenNames(result.secondSubmission()));
    }

    /**
     * Creates the tests for the average deviation.
     * @param deltaStatistics The deltas
     * @return The list of tests
     */
    private List<DynamicTest> evaluateDeviationOfSimilarity(DeltaSummaryStatistics deltaStatistics) {
        return List.of(deviationOfSimilarityTest("positive", deltaStatistics.getPositiveStatistics()),
                deviationOfSimilarityTest("negative", deltaStatistics.getNegativeStatistics()));
    }

    private DynamicTest deviationOfSimilarityTest(String textualSign, DoubleSummaryStatistics statistics) {
        return DynamicTest.dynamicTest("OVERVIEW: " + textualSign + " similarity deviation", () -> {
            if (Math.abs(statistics.getAverage()) > EPSILON) {
                fail(textualSign + " deviation over all AVG similarity values:" + System.lineSeparator() + statistics);
            }
        });
    }

    private List<String> getTokenNames(Submission submission) {
        return submission.getTokenList().stream().map(it -> {
            if (Enum.class.isAssignableFrom(it.getType().getClass())) {
                return ((Enum<?>) it.getType()).name();
            }
            return it.getType().getDescription();
        }).toList();
    }
}
