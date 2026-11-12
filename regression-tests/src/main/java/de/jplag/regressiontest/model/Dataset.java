package de.jplag.regressiontest.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import de.jplag.Language;
import de.jplag.regressiontest.constants.TestDirectoryConstants;
import de.jplag.regressiontest.helper.LanguageDeserializer;
import de.jplag.regressiontest.helper.UnzipManager;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents a dataset for the regression test suite.
 * @param language the programming language of the dataset
 * @param format the format of the dataset
 * @param sourceDirectory The source directory
 * @param resultFile The result file name
 * @param goldStandardFile The gold standard file
 * @param goldStandardDelimiter optional delimiter used in the gold standard file
 * @param options The options for the JPlag runs
 */
public record Dataset(Language language, DatasetFormat format, Path sourceDirectory, Path resultFile, Optional<Path> goldStandardFile,
        String goldStandardDelimiter, Options options) {

    private static final String DEFAULT_GOLD_STANDARD_DELIMITER = ";";
    private static final String DEFAULT_RESULT_FILE_NAME = "%s.json";

    /**
     * @param name the dataset name (required)
     * @param language the programming language of the dataset (required)
     * @param format the format of the dataset (required)
     * @param sourceLocation The source directory, may be null
     * @param resultFile The result file name, may be null
     * @param goldStandardFile The gold standard file, may be null
     * @param goldStandardDelimiter optional delimiter used in the gold standard file
     * @param storageFormat optional storage format
     * @param options The options for the JPlag runs, may be null
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Dataset(@JsonProperty(value = "name", required = true) String name,
            @JsonDeserialize(using = LanguageDeserializer.class) @JsonProperty(value = "language", required = true) Language language,
            @JsonProperty(value = "format", required = true) DatasetFormat format, @JsonProperty("sourceLocation") String sourceLocation,
            @JsonProperty("storageFormat") StorageFormat storageFormat, @JsonProperty("resultFile") String resultFile,
            @JsonProperty("goldStandardFile") String goldStandardFile, @JsonProperty("goldStandardDelimiter") String goldStandardDelimiter,
            @JsonProperty("options") Options options) throws IOException {
        Path actualSourceDirectory = actualSourceDirectory(storageFormat, sourceLocation, name);
        Optional<Path> actualGoldStandardFile = Optional.ofNullable(goldStandardFile).map(actualSourceDirectory::resolve);

        String targetFile = Objects.requireNonNullElseGet(resultFile, () -> String.format(DEFAULT_RESULT_FILE_NAME, name));
        Path actualResultFile = TestDirectoryConstants.BASE_PATH_TO_RESULT_JSON.resolve(targetFile);

        this(language, format, actualSourceDirectory, actualResultFile, actualGoldStandardFile,
                Objects.requireNonNullElse(goldStandardDelimiter, DEFAULT_GOLD_STANDARD_DELIMITER),
                Objects.requireNonNullElseGet(options, Options::new));
    }

    /**
     * Gets the source directories.
     * @return The source directories
     */
    public Set<Path> getSourceDirectories() throws IOException {
        return new HashSet<>(format.getSourceDirectories(this));
    }

    /**
     * Helper function replacing null by the default value.
     * @return The source directory
     */
    private static Path actualSourceDirectory(StorageFormat storageFormat, String sourceLocation, String name) throws IOException {
        StorageFormat actualStorageFormat = storageFormat == null ? StorageFormat.DIRECTORY : storageFormat;

        String location = sourceLocation;
        if (location == null) {
            location = String.format(actualStorageFormat.getDefaultSource(), name);
        }
        Path sourcePath = TestDirectoryConstants.BASE_PATH_TO_RESOURCES.resolve(location);

        return switch (actualStorageFormat) {
            case DIRECTORY -> sourcePath;
            case ZIP -> UnzipManager.unzipOrCache(name, sourcePath);
        };

    }
}
