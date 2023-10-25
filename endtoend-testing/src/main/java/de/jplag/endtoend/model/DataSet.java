package de.jplag.endtoend.model;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.jplag.Language;
import de.jplag.endtoend.constants.TestDirectoryConstants;
import de.jplag.endtoend.helper.LanguageDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents a data set for the endToEnd test suite.
 * @param name The name of the data set
 * @param language The language
 * @param format The format
 * @param sourceDirectory The source directory, may be null
 * @param resultFile The result file name, may be null
 * @param goldStandardFile The gold standard file, may be null
 * @param options The options for the jplag runs, may be null
 */
public record DataSet(@JsonProperty(required = true) String name,
        @JsonDeserialize(using = LanguageDeserializer.class) @JsonProperty(required = true) Language language,
        @JsonProperty(required = true) DataSetFormat format, @JsonProperty String sourceDirectory, @JsonProperty String resultFile,
        @JsonProperty String goldStandardFile, @JsonProperty String goldStandardDelimiter, @JsonProperty Options options) {

    private static final String DEFAULT_GOLD_STANDARD_DELIMITER = ";";
    private static final String DEFAULT_SOURCE_DIRECTORY = "data/%s";
    private static final String DEFAULT_RESULT_FILE_NAME = "%s.json";

    /**
     * Gets the source directories
     * @return The source directories
     */
    public Set<File> getSourceDirectories() {
        return format.getSourceDirectories(this).stream()
                .map(file -> new File(TestDirectoryConstants.BASE_PATH_TO_RESOURCES.toFile(), file.getPath())).collect(Collectors.toSet());
    }

    /**
     * Helper function replacing null by the default value
     * @return The source directory
     */
    String actualSourceDirectory() {
        if (sourceDirectory == null) {
            return String.format(DEFAULT_SOURCE_DIRECTORY, this.name);
        }
        return sourceDirectory;
    }

    /**
     * Helper function replacing null by the default value
     * @return The result file
     */
    public File getResultFile() {
        if (resultFile == null) {
            return new File(TestDirectoryConstants.BASE_PATH_TO_RESULT_JSON.toFile(), String.format(DEFAULT_RESULT_FILE_NAME, this.name));
        } else {
            return new File(TestDirectoryConstants.BASE_PATH_TO_RESULT_JSON.toFile(), resultFile);
        }
    }

    /**
     * @return The gold standard file as an optional.
     */
    public Optional<File> getGoldStandardFile() {
        return Optional.ofNullable(this.goldStandardFile).map(name -> new File(TestDirectoryConstants.BASE_PATH_TO_RESOURCES.toFile(), name));
    }

    /**
     * Helper function replacing null by the default value.
     * @return The options
     */
    public Options getOptions() {
        return Objects.requireNonNullElseGet(this.options, Options::new);
    }

    /**
     * Returns the actual delimiter, replacing null by the default value
     */
    public String getActualDelimiter() {
        return Objects.requireNonNullElse(this.goldStandardDelimiter, DEFAULT_GOLD_STANDARD_DELIMITER);
    }
}
