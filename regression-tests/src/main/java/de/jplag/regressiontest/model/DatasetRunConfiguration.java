package de.jplag.regressiontest.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.options.JPlagOptions;
import de.jplag.options.ParsingOptions;
import de.jplag.options.SubmissionFileOptions;

/**
 * A run configuration for the regression tests.
 * @param jPlagOptions The jplag options to use
 * @param identifier The identifier for the configuration
 */
public record DatasetRunConfiguration(JPlagOptions jPlagOptions, String identifier) {
    private static final String IDENTIFIER_FORMAT = "MTM: %s";

    /**
     * Builds all configurations for a dataset.
     * @param dataset The dataset
     * @return The configurations
     */
    public static List<DatasetRunConfiguration> generateRunConfigurations(Dataset dataset) throws IOException {
        Options configuredOptions = dataset.options();
        List<DatasetRunConfiguration> result = new ArrayList<>();

        for (int minimumTokenMatch : configuredOptions.getMinimumTokenMatches()) {
            Path baseCode = null;
            if (configuredOptions.baseCodeDirectory() != null) {
                baseCode = dataset.format().getBaseCodeDirectory(dataset, configuredOptions.baseCodeDirectory());
            }
            JPlagOptions options = new JPlagOptions(
                    new SubmissionFileOptions(dataset.getSourceDirectories(), Set.of(), baseCode),
                    new ParsingOptions(dataset.language(), minimumTokenMatch, false, false, false, ParsingOptions.DEFAULT_SIMILARITY_METRIC, ParsingOptions.DEFAULT_SIMILARITY_THRESHOLD)
            );
            result.add(new DatasetRunConfiguration(options, String.format(IDENTIFIER_FORMAT, minimumTokenMatch)));
        }

        return result;
    }
}
