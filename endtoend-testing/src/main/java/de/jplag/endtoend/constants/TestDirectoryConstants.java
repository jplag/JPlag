package de.jplag.endtoend.constants;

import java.nio.file.Path;

/**
 * All constant values that are needed in the test cases or helper classes.
 */
public final class TestDirectoryConstants {

    private TestDirectoryConstants() {
        // private constructor to prevent instantiation
    }

    /**
     * Base path to the saved results
     */
    public static final Path BASE_PATH_TO_RESULT_JSON = Path.of("src", "test", "resources", "results");

    /**
     * Base path to the data set descriptors
     */
    public static final Path BASE_PATH_TO_DATA_SET_DESCRIPTORS = Path.of("src", "test", "resources", "dataSets");

    /**
     * Base path to the resources directory
     */
    public static final Path BASE_PATH_TO_RESOURCES = Path.of("src", "test", "resources");
}
