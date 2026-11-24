package de.jplag.regressiontest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.regressiontest.constants.TestDirectoryConstants;
import de.jplag.regressiontest.helper.FileHelper;
import de.jplag.regressiontest.helper.TestSuiteHelper;
import de.jplag.regressiontest.model.ComparisonIdentifier;
import de.jplag.regressiontest.model.Dataset;
import de.jplag.regressiontest.model.DatasetRunConfiguration;
import de.jplag.regressiontest.model.ExpectedResult;
import de.jplag.regressiontest.model.GoldStandard;
import de.jplag.regressiontest.model.ResultDescription;
import de.jplag.reporting.serialization.JacksonUtils;

import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Test class for automatically generating the JSON file describing the expected results. To generate a result JSON,
 * adapt the DATA_SET constant.
 */
class RegressionGeneratorTest {
    private static final String DATA_SET = "progpedia";

    private static final Logger logger = LoggerFactory.getLogger(RegressionGeneratorTest.class);

    @Disabled("only enable to generate result json file")
    @Test
    @DisplayName("Generator test to produce new expected E2E results if behavior of JPlag changes.")
    void generateResultJson() throws ExitException, IOException {
        Dataset dataset = JacksonUtils.createNewObjectMapper()
                .readValue(new File(TestDirectoryConstants.BASE_PATH_TO_DATA_SET_DESCRIPTORS.toFile(), DATA_SET + ".json"), Dataset.class);
        List<ResultDescription> resultDescriptions = new ArrayList<>();

        for (DatasetRunConfiguration runConfiguration : DatasetRunConfiguration.generateRunConfigurations(dataset)) {
            JPlagOptions options = runConfiguration.jPlagOptions();
            JPlagResult result = JPlag.run(options);
            List<JPlagComparison> comparisons = result.getAllComparisons();
            Map<String, ExpectedResult> expectedResults = comparisons.stream()
                    .collect(Collectors.toMap(TestSuiteHelper::getTestIdentifier, ExpectedResult::fromComparison));

            GoldStandard goldStandard = null;
            if (dataset.goldStandardFile().isPresent()) {
                goldStandard = GoldStandard.buildFromComparisons(comparisons,
                        ComparisonIdentifier.loadIdentifiersFromFile(dataset.goldStandardFile().get(), dataset.goldStandardDelimiter()));
            }

            resultDescriptions.add(new ResultDescription(runConfiguration.identifier(), expectedResults, goldStandard));
        }

        Path outputFile = writeJsonModelsToJsonFile(resultDescriptions, dataset);
        logger.info("result JSON written to file '{}'", outputFile);

        assertTrue(Files.exists(outputFile), "Output JSON file was not created");
        assertFalse(resultDescriptions.isEmpty(), "No result descriptions generated");
    }

    /**
     * Saves the passed object as a JSON file to the file identified by the test suite and language. Returns that file.
     * @param resultDescriptions list of elements to be saved
     * @param dataset The dataset the elements are for
     * @throws IOException Signals that an I/O exception, of some sort, has occurred. This class is the general class of
     * exceptions produced by failed or interrupted I/O operations.
     */
    private static Path writeJsonModelsToJsonFile(List<ResultDescription> resultDescriptions, Dataset dataset) throws IOException {
        ObjectWriter writer = JacksonUtils.createNewObjectMapper().writer().withDefaultPrettyPrinter();
        Path outputFile = dataset.resultFile();

        FileHelper.createDirectoryIfItDoesNotExist(outputFile.getParent());
        FileHelper.createFileIfItDoesNotExist(outputFile);

        writer.writeValue(Files.newOutputStream(outputFile), resultDescriptions.toArray());
        return outputFile;

    }
}
