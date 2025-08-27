package de.jplag.endtoend.model;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import de.jplag.Language;
import de.jplag.endtoend.constants.TestDirectoryConstants;
import de.jplag.endtoend.helper.LanguageDeserializer;
import de.jplag.endtoend.helper.UnzipManager;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents a data set for the end-to-end test suite.
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
public record DataSet(@JsonProperty(required = true) String name,
        @JsonDeserialize(using = LanguageDeserializer.class) @JsonProperty(required = true) Language language,
        @JsonProperty(required = true) DataSetFormat format, @JsonProperty String sourceLocation, @JsonProperty StorageFormat storageFormat,
        @JsonProperty String resultFile, @JsonProperty String goldStandardFile, @JsonProperty String goldStandardDelimiter,
        @JsonProperty Options options) {

    private static final String DEFAULT_GOLD_STANDARD_DELIMITER = ";";
    private static final String DEFAULT_SOURCE_DIRECTORY = "data/%s";
    private static final String DEFAULT_SOURCE_ZIP = "data/%s.zip";
    private static final String DEFAULT_RESULT_FILE_NAME = "%s.json";

    /**
     * Gets the source directories.
     * @return The source directories
     */
    public Set<File> getSourceDirectories() throws IOException {
        return new HashSet<>(format.getSourceDirectories(this));
    }

    /**
     * Helper function replacing null by the default value.
     * @return The source directory
     */
    File actualSourceDirectory() throws IOException {
        StorageFormat actualStorageFormat = storageFormat == null ? StorageFormat.DIRECTORY : storageFormat;

        if (actualStorageFormat == StorageFormat.DIRECTORY) {
            String location = sourceLocation;
            if (location == null) {
                location = String.format(DEFAULT_SOURCE_DIRECTORY, this.name);
            }
            return new File(TestDirectoryConstants.BASE_PATH_TO_RESOURCES.toFile(), location);
        }
        if (actualStorageFormat == StorageFormat.ZIP) {
            String location = sourceLocation;
            if (location == null) {
                location = String.format(DEFAULT_SOURCE_ZIP, this.name);
            }
            return UnzipManager.unzipOrCache(this, new File(TestDirectoryConstants.BASE_PATH_TO_RESOURCES.toFile(), location));
        }

        throw new IllegalStateException();
    }

    /**
     * Helper function replacing null by the default value.
     * @return The result file
     */
    public File getResultFile() {
        if (resultFile == null) {
            return new File(TestDirectoryConstants.BASE_PATH_TO_RESULT_JSON.toFile(), String.format(DEFAULT_RESULT_FILE_NAME, this.name));
        }
        return new File(TestDirectoryConstants.BASE_PATH_TO_RESULT_JSON.toFile(), resultFile);
    }

    /**
     * @return The gold standard file as an optional.
     */
    public Optional<File> getGoldStandardFile() throws IOException {
        File actualSourceDirectory = this.actualSourceDirectory();
        return Optional.ofNullable(this.goldStandardFile).map(name -> new File(actualSourceDirectory, name));
    }

    /**
     * Helper function replacing null by the default value.
     * @return The options
     */
    public Options getOptions() {
        return Objects.requireNonNullElseGet(this.options, Options::new);
    }

    /**
     * Returns the actual delimiter, replacing null by the default value.
     */
    public String getActualDelimiter() {
        return Objects.requireNonNullElse(this.goldStandardDelimiter, DEFAULT_GOLD_STANDARD_DELIMITER);
    }
}
