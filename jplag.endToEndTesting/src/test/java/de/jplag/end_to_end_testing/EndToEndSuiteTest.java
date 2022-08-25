package de.jplag.end_to_end_testing;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringJoiner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.end_to_end_testing.helper.FileHelper;
import de.jplag.end_to_end_testing.helper.JsonHelper;
import de.jplag.end_to_end_testing.helper.TestSuiteHelper;
import de.jplag.end_to_end_testing.model.*;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

/**
 * Main test class for end-to-end testing in all language. The test cases aim to detect changes in the detection of
 * plagiarism in the Java language and to be able to roughly categorize them. The plagiarism is compared with the
 * original class. The results are compared with the results from previous tests and changes are detected.
 */
public class EndToEndSuiteTest {
    // Language -> directory names and Paths
    private Map<Language, Map<String, Path>> LanguageToTestCaseMapper;

    private List<Options> options;

    private static Map<String, List<ResultDescription>> temporaryResultList;
    private static List<String> validationErrors;
    private static Language languageOption;

    public EndToEndSuiteTest() throws IOException {
        // Loading the test resources
        LanguageToTestCaseMapper = TestSuiteHelper.getAllLanguageResources();
        // creating the temporary lists for the test run
        validationErrors = new ArrayList<>();
        temporaryResultList = new HashMap<>();
        // creating options object for the testSuite
        setRunOptions();
    }

    /**
     * creating the required options object for the endToEnd tests
     */
    private void setRunOptions() {
        options = new ArrayList<>();
        options.add(new Options(1));
        options.add(new Options(15));
    }

    /**
     * Creates the result json files based on the current test results after the test run.
     * @throws IOException is thrown for all problems that may occur while parsing the json file. This includes both reading
     */
    @AfterAll
    public static void tearDown() throws IOException {
        for (var resultDescriptionItem : temporaryResultList.entrySet()) {
            JsonHelper.writeJsonModelsToJsonFile(resultDescriptionItem.getValue(), resultDescriptionItem.getKey(), languageOption);
        }
    }

    /**
     * Creates the test cases over all language options for which data is available and the current test options.
     * @return dynamic test cases across all test data and languages
     * @throws IOException is thrown for all problems that may occur while parsing the json file. This includes both reading
     */
    @TestFactory
    Collection<DynamicTest> dynamicOverAllTest() throws IOException {
        for (Entry<LanguageOption, Map<String, Path>> languageMap : LanguageToTestCaseMapper.entrySet()) {
            LanguageOption currentLanguageOption = languageMap.getKey();
            for (Entry<String, Path> languagePaths : languageMap.getValue().entrySet()) {
                String[] fileNames = FileHelper.loadAllTestFileNames(languagePaths.getValue());
                var testCases = TestSuiteHelper.getTestCases(fileNames, languagePaths.getValue());
                var testCollection = new ArrayList<DynamicTest>();
                String directoryName = languagePaths.getValue().getFileName().toString();
                List<ResultDescription> tempResult = JsonHelper.getJsonModelListFromPath(directoryName, currentLanguageOption);
                languageOption = currentLanguageOption;
                for (Options option : options) {
                    for (var testCase : testCases) {
                        Optional<ResultDescription> currentResultDescription = tempResult.stream().filter(x -> x.options().equals(option))
                                .findFirst();
                        testCollection.add(DynamicTest.dynamicTest(getTestCaseDisplayName(option, currentLanguageOption, testCase), () -> {
                            runTests(directoryName, option, currentLanguageOption, testCase, currentResultDescription);
                        }));
                    }
                }
                return testCollection;
            }
        }
        return null;
    }

    /**
     * Superordinate test function to be able to continue to check all data to be tested in case of failed tests
     * @param directoryName name of the current tested directory
     * @param options for the current test run
     * @param languageOption current JPlag language option
     * @param testFiles files to be tested
     * @param currentResultDescription results stored for the test data
     * @throws IOException Signals that an I/O exception of some sort has occurred. Thisclass is the general class of
     * exceptions produced by failed orinterrupted I/O operations
     * @throws ExitException Exceptions for problems during the execution of JPlag that lead to an preemptive exit.
     */
    private void runTests(String directoryName, Options option, LanguageOption currentLanguageOption, String[] testFiles,
            Optional<ResultDescription> currentResultDescription) throws IOException, ExitException {
        try {
            ResultDescription currentResult = currentResultDescription.orElse(null);
            runJPlagTestSuite(directoryName, option, currentLanguageOption, testFiles, currentResult);
        } finally {
            validationErrors.clear();
            TestSuiteHelper.clear();
        }
    }

    /**
     * EndToEnd test for the passed objects
     * @param directoryName name of the current tested directory
     * @param options for the current test run
     * @param languageOption current JPlag language option
     * @param testFiles files to be tested
     * @param currentResultDescription results stored for the test data
     * @throws IOException Signals that an I/O exception of some sort has occurred. Thisclass is the general class of
     * exceptions produced by failed orinterrupted I/O operations
     * @throws ExitException Exceptions for problems during the execution of JPlag that lead to an preemptive exit.
     */
    private void runJPlagTestSuite(String directoryName, Options options, LanguageOption languageOption, String[] testFiles,
            ResultDescription currentResultDescription) throws IOException, ExitException {
        String[] submissionPath = FileHelper.createNewTestCaseDirectory(testFiles);

        JPlagOptions jplagOptions = new JPlagOptions(Arrays.asList(submissionPath), new ArrayList<>(), languageOption);

        jplagOptions.setMinimumTokenMatch(options.minimumTokenMatch());

        JPlagResult jplagResult = new JPlag(jplagOptions).run();

        List<JPlagComparison> currentJPlagComparison = jplagResult.getAllComparisons();

        for (JPlagComparison jPlagComparison : currentJPlagComparison) {
            String identifier = TestSuiteHelper.getTestIdentifier(jPlagComparison);
            addToTemporaryResultMap(directoryName, options, jPlagComparison, languageOption);

            assertNotNull(currentResultDescription, "No stored result could be found for the current LanguageOption! " + options.toString());

            ExpectedResult result = currentResultDescription.getExpectedResultByIdentifier(TestSuiteHelper.getTestIdentifier(jPlagComparison));
            assertNotNull(result, "No stored result could be found for the identifier! " + identifier);

            if (Float.compare(result.resultSimilarityMinimum(), jPlagComparison.minimalSimilarity()) != 0) {
                addToValidationErrors("minimalSimilarity", String.valueOf(result.resultSimilarityMinimum()),
                        String.valueOf(jPlagComparison.minimalSimilarity()));
            }
            if (Float.compare(result.resultSimilarityMaximum(), jPlagComparison.maximalSimilarity()) != 0) {
                addToValidationErrors("maximalSimilarity", String.valueOf(result.resultSimilarityMaximum()),
                        String.valueOf(jPlagComparison.maximalSimilarity()));
            }
            if (Integer.compare(result.resultMatchedTokenNumber(), jPlagComparison.getNumberOfMatchedTokens()) != 0) {
                addToValidationErrors("numberOfMatchedTokens", String.valueOf(result.resultMatchedTokenNumber()),
                        String.valueOf(jPlagComparison.getNumberOfMatchedTokens()));
            }

            assertTrue(validationErrors.isEmpty(), createValidationErrorOutout());
        }
    }

    /**
     * Creates the display message for failed tests
     * @param valueName Name of the failed test object
     * @param currentValue current test values
     * @param expectedValue expected test values
     */
    private void addToValidationErrors(String valueName, String currentValue, String expectedValue) {
        validationErrors.add(valueName + " was " + currentValue + " but expected " + expectedValue);
    }

    /**
     * Creates the display info from the current failed test results
     * @return formatted text for the failed comparative values of the current test
     */
    private String createValidationErrorOutout() {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        joiner.add(""); // empty line at start
        joiner.add("There were <" + validationErrors.size() + "> validation error(s):");

        validationErrors.stream().forEach(errorLine -> joiner.add(errorLine));
        joiner.add(""); // line break at the end

        return joiner.toString();
    }

    /**
     * Add or create the current test results in a temporary list to be able to save them later.
     * @param directoryName name of the current tested directory
     * @param options for the current test run
     * @param jPlagComparison current test results
     * @param languageOption current JPlag language option
     */
    private void addToTemporaryResultMap(String directoryName, Options options, JPlagComparison jPlagComparison, LanguageOption languageOption) {
        var element = temporaryResultList.get(directoryName);
        if (element != null) {
            for (var item : element) {
                if (item.options().equals(options)) {
                    item.putIdentifierToResultMap(TestSuiteHelper.getTestIdentifier(jPlagComparison), new ExpectedResult(
                            jPlagComparison.minimalSimilarity(), jPlagComparison.maximalSimilarity(), jPlagComparison.getNumberOfMatchedTokens()));
                    return;
                }
            }
            Map<String, ExpectedResult> temporaryHashMap = new HashMap<>();
            temporaryHashMap.put(TestSuiteHelper.getTestIdentifier(jPlagComparison), new ExpectedResult(jPlagComparison.minimalSimilarity(),
                    jPlagComparison.maximalSimilarity(), jPlagComparison.getNumberOfMatchedTokens()));
            element.add(new ResultDescription(languageOption, options, temporaryHashMap));
        } else {
            var temporaryNewResultList = new ArrayList<ResultDescription>();
            Map<String, ExpectedResult> temporaryHashMap = new HashMap<>();
            temporaryHashMap.put(TestSuiteHelper.getTestIdentifier(jPlagComparison), new ExpectedResult(jPlagComparison.minimalSimilarity(),
                    jPlagComparison.maximalSimilarity(), jPlagComparison.getNumberOfMatchedTokens()));

            temporaryNewResultList.add(new ResultDescription(languageOption, options, temporaryHashMap));

            temporaryResultList.put(directoryName, temporaryNewResultList);
        }
    }

    /**
     * Creates the name of the test for better assignment and readability Pattern: Language: (option values)
     * filename1-filename2
     * @param option under which the current test run
     * @param languageOption current language used in the test
     * @param testFiles test data for assigning by filename
     * @return display name for the individual tests
     */
    private String getTestCaseDisplayName(Options option, LanguageOption languageOption, String[] testFiles) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(" + String.valueOf(option.minimumTokenMatch()) + ")");
        for (int counter = 0; counter < testFiles.length; counter++) {
            String fileName = Path.of(testFiles[counter]).toFile().getName();
            stringBuilder.append(fileName.substring(0, fileName.lastIndexOf('.')));
            if (counter + 1 < testFiles.length) {
                stringBuilder.append("-");
            }
        }
        return languageOption.toString() + ": " + stringBuilder.toString();
    }
}
