package de.jplag.endtoend.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.options.JPlagOptions;

/**
 * A run configuration for the end-to-end tests.
 * @param jPlagOptions The jplag options to use
 * @param identifier The identifier for the configuration
 */
public record DataSetRunConfiguration(JPlagOptions jPlagOptions, String identifier) {
    private static final String IDENTIFIER_FORMAT = "MTM: %s";

    /**
     * Builds all configurations for a data set.
     * @param dataSet The data set
     * @return The configurations
     */
    public static List<DataSetRunConfiguration> generateRunConfigurations(DataSet dataSet) throws IOException {
        Options configuredOptions = dataSet.getOptions();
        List<DataSetRunConfiguration> result = new ArrayList<>();

        for (int minimumTokenMatch : configuredOptions.getMinimumTokenMatches()) {
            JPlagOptions options = new JPlagOptions(dataSet.language(), dataSet.getSourceDirectories(), Set.of());
            options = options.withMinimumTokenMatch(minimumTokenMatch);
            if (configuredOptions.baseCodeDirectory() != null) {
                File baseCode = dataSet.format().getBaseCodeDirectory(dataSet, configuredOptions.baseCodeDirectory());
                options = options.withBaseCodeSubmissionDirectory(baseCode);
            }
            result.add(new DataSetRunConfiguration(options, String.format(IDENTIFIER_FORMAT, minimumTokenMatch)));
        }

        return result;
    }
}
