package de.jplag.endtoend;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
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
import de.jplag.endtoend.constants.TestDirectoryConstants;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Test class for automatically generating the JSON file describing the expected results. To generate a result JSON,
 * adapt the DATA_SET constant.
 */
class EndToEndGeneratorTest {
    private static final String DATA_SET = "progpedia";

    private static final Logger logger = LoggerFactory.getLogger(EndToEndGeneratorTest.class);

    @Disabled("only enable to generate result json file")
    @Test
    @DisplayName("Generator test to produce new expected E2E results if behavior of JPlag changes.")
    void generateResultJson() throws ExitException, IOException {
        DataSet dataSet = new ObjectMapper()
                .readValue(new File(TestDirectoryConstants.BASE_PATH_TO_DATA_SET_DESCRIPTORS.toFile(), DATA_SET + ".json"), DataSet.class);
        List<ResultDescription> resultDescriptions = new ArrayList<>();

        for (DataSetRunConfiguration runConfiguration : DataSetRunConfiguration.generateRunConfigurations(dataSet)) {
            JPlagOptions options = runConfiguration.jPlagOptions();
            JPlagResult result = JPlag.run(options);
            List<JPlagComparison> comparisons = result.getAllComparisons();
            Map<String, ExpectedResult> expectedResults = comparisons.stream()
                    .collect(Collectors.toMap(TestSuiteHelper::getTestIdentifier, ExpectedResult::fromComparison));

            GoldStandard goldStandard = null;
            if (dataSet.getGoldStandardFile().isPresent()) {
                goldStandard = GoldStandard.buildFromComparisons(comparisons,
                        ComparisonIdentifier.loadIdentifiersFromFile(dataSet.getGoldStandardFile().get(), dataSet.getActualDelimiter()));
            }

            resultDescriptions.add(new ResultDescription(runConfiguration.identifier(), expectedResults, goldStandard));
        }

        File outputFile = writeJsonModelsToJsonFile(resultDescriptions, dataSet);
        logger.info("result JSON written to file '{}'", outputFile);

        assertTrue(outputFile.exists(), "Output JSON file was not created");
        assertFalse(resultDescriptions.isEmpty(), "No result descriptions generated");
    }

    /**
     * Saves the passed object as a json file to the file identified by the test suite and language. Returns that file.
     * @param resultDescriptions list of elements to be saved
     * @param dataSet The data set the elements are for
     * @throws IOException Signals that an I/O exception, of some sort, has occurred. Thisclass is the general class of
     * exceptions produced by failed orinterrupted I/O operations.
     */
    private static File writeJsonModelsToJsonFile(List<ResultDescription> resultDescriptions, DataSet dataSet) throws IOException {
        ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        File outputFile = dataSet.getResultFile();

        FileHelper.createDirectoryIfItDoesNotExist(outputFile.getParentFile());
        FileHelper.createFileIfItDoesNotExist(outputFile);

        writer.writeValue(outputFile, resultDescriptions.toArray());
        return outputFile;

    }
}
