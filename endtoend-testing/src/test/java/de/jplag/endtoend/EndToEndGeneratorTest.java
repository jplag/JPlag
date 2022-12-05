package de.jplag.endtoend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.cli.LanguageLoader;
import de.jplag.endtoend.constants.TestDirectoryConstants;
import de.jplag.endtoend.helper.FileHelper;
import de.jplag.endtoend.helper.TestSuiteHelper;
import de.jplag.endtoend.model.ExpectedResult;
import de.jplag.endtoend.model.Options;
import de.jplag.endtoend.model.ResultDescription;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Test class for automatically generating the json file describing the expected results. To generate a result json,
 * adapt the three constants to your requirements and enable the test case.
 */
class EndToEndGeneratorTest {
    private static final String LANGUAGE_IDENTIFIER = "java";
    private static final String TEST_SUITE_IDENTIFIER = "sortAlgo";
    private static final List<Options> OPTIONS = List.of(new Options(3), new Options(9));

    private static final Logger logger = LoggerFactory.getLogger(EndToEndGeneratorTest.class);

    @Disabled("only enable to generate result json file")
    @Test
    void generateResultJson() throws ExitException, IOException {
        Language language = LanguageLoader.getLanguage(LANGUAGE_IDENTIFIER).orElseThrow();
        File submissionDirectory = TestSuiteHelper.getSubmissionDirectory(language, TEST_SUITE_IDENTIFIER);
        List<ResultDescription> resultDescriptions = new ArrayList<>();
        for (var option : OPTIONS) {
            JPlagOptions jplagOptions = new JPlagOptions(language, Set.of(submissionDirectory), Set.of())
                    .withMinimumTokenMatch(option.minimumTokenMatch());
            JPlagResult jplagResult = new JPlag(jplagOptions).run();
            List<JPlagComparison> jPlagComparisons = jplagResult.getAllComparisons();
            Map<String, ExpectedResult> expectedResults = jPlagComparisons.stream()
                    .collect(Collectors.toMap(TestSuiteHelper::getTestIdentifier, comparison -> new ExpectedResult(comparison.minimalSimilarity(),
                            comparison.maximalSimilarity(), comparison.getNumberOfMatchedTokens())));
            resultDescriptions.add(new ResultDescription(language.getIdentifier(), option, expectedResults));
        }
        File outputFile = writeJsonModelsToJsonFile(resultDescriptions, TEST_SUITE_IDENTIFIER, LANGUAGE_IDENTIFIER);
        logger.info("result JSON written to file '{}'", outputFile);
    }

    /**
     * Saves the passed object as a json file to the file identified by the test suite and language. Returns that file.
     * @param resultDescriptions list of elements to be saved
     * @param testSuiteIdentifier identifier of the test suite
     * @param languageIdentifier identifier of the language
     * @throws IOException Signals that an I/O exception of some sort has occurred. Thisclass is the general class of
     * exceptions produced by failed orinterrupted I/O operations.
     */
    private static File writeJsonModelsToJsonFile(List<ResultDescription> resultDescriptions, String testSuiteIdentifier, String languageIdentifier)
            throws IOException {
        ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        File outputFile = TestDirectoryConstants.TEMPORARY_SUBMISSION_DIRECTORY_NAME.resolve(languageIdentifier)
                .resolve(testSuiteIdentifier + ".json").toFile();

        FileHelper.createDirectoryIfItDoesNotExist(outputFile.getParentFile());
        FileHelper.createFileIfItDoesNotExist(outputFile);

        // convert book object to JSON file

        writer.writeValue(outputFile, resultDescriptions.toArray());
        return outputFile;

    }
}
