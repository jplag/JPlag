package de.jplag.endtoend.model;

import java.io.File;
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
        @JsonProperty String goldStandardFile, @JsonProperty Options options) {

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
            return "data/" + name;
        }
        return sourceDirectory;
    }

    /**
     * Helper function replacing null by the default value
     * @return The result file
     */
    public File getResultFile() {
        if (resultFile == null) {
            return new File(TestDirectoryConstants.BASE_PATH_TO_RESULT_JSON.toFile(), name + ".json");
        } else {
            return new File(TestDirectoryConstants.BASE_PATH_TO_RESULT_JSON.toFile(), resultFile);
        }
    }

    /**
     * @return The gold standard file. Can be null.
     */
    public File getGoldStandardFile() {
        if (goldStandardFile == null) {
            return null;
        }

        return new File(TestDirectoryConstants.BASE_PATH_TO_RESOURCES.toFile(), goldStandardFile);
    }

    /**
     * Helper function replacing null by the default value.
     * @return The options
     */
    public Options getOptions() {
        if (this.options != null) {
            return this.options;
        } else {
            return new Options(null, null);
        }
    }
}
