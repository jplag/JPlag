package de.jplag.regressiontest.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.options.JPlagOptions;

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
        Options configuredOptions = dataset.getOptions();
        List<DatasetRunConfiguration> result = new ArrayList<>();

        for (int minimumTokenMatch : configuredOptions.getMinimumTokenMatches()) {
            JPlagOptions options = new JPlagOptions(dataset.language(), dataset.getSourceDirectories(), Set.of());
            options = options.withMinimumTokenMatch(minimumTokenMatch);
            if (configuredOptions.baseCodeDirectory() != null) {
                Path baseCode = dataset.format().getBaseCodeDirectory(dataset, configuredOptions.baseCodeDirectory());
                options = options.withBaseCodeSubmissionDirectory(baseCode);
            }
            result.add(new DatasetRunConfiguration(options, String.format(IDENTIFIER_FORMAT, minimumTokenMatch)));
        }

        return result;
    }
}
