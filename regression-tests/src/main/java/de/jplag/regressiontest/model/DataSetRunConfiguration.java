package de.jplag.regressiontest.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.jplag.exceptions.RootDirectoryException;
import de.jplag.inputs.FileSystemSingleSubmissionDirectory;
import de.jplag.inputs.FileSystemSubmissionDirectory;
import de.jplag.inputs.SubmissionDirectory;
import de.jplag.options.JPlagOptions;

/**
 * A run configuration for the regression tests.
 *
 * @param jPlagOptions The jplag options to use
 * @param identifier   The identifier for the configuration
 */
public record DataSetRunConfiguration(JPlagOptions jPlagOptions, String identifier) {
    private static final String IDENTIFIER_FORMAT = "MTM: %s";

    /**
     * Builds all configurations for a data set.
     *
     * @param dataSet The data set
     * @return The configurations
     */
    public static List<DataSetRunConfiguration> generateRunConfigurations(DataSet dataSet) throws IOException, RootDirectoryException {
        Options configuredOptions = dataSet.getOptions();
        List<DataSetRunConfiguration> result = new ArrayList<>();

        for (int minimumTokenMatch : configuredOptions.getMinimumTokenMatches()) {
            Set<SubmissionDirectory> directories = new HashSet<>();
            for (File it : dataSet.getSourceDirectories()) {
                directories.add(new FileSystemSubmissionDirectory(it, it.getPath()));
            }
            JPlagOptions options = new JPlagOptions(dataSet.language(), directories, Set.of());
            options = options.withMinimumTokenMatch(minimumTokenMatch);
            if (configuredOptions.baseCodeDirectory() != null) {
                File baseCode = dataSet.format().getBaseCodeDirectory(dataSet, configuredOptions.baseCodeDirectory());
                FileSystemSingleSubmissionDirectory baseCodeDirectory = new FileSystemSingleSubmissionDirectory(baseCode, "baseCode");
                options.withOldSubmissionDirectories(Set.of(baseCodeDirectory));
                options = options.withBaseCodeSubmission(baseCodeDirectory.getSubmissionIdentifer());
            }
            result.add(new DataSetRunConfiguration(options, String.format(IDENTIFIER_FORMAT, minimumTokenMatch)));
        }

        return result;
    }
}
