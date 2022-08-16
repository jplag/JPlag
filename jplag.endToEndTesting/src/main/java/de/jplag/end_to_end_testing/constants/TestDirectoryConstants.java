package de.jplag.end_to_end_testing.constants;

import java.nio.file.Path;

/**
 * All constant values that are needed in the test cases or helper classes.
 */
public final class TestDirectoryConstants {

//    private TestDirectoryConstants() {
//        // private constructor to prevent instantiation
//    }
//
    /**
     * Create the complete path to the temporary result files. Here the temporary system path is extended with the
     * "RESULT_DIRECTORY_NAME", which is predefined in this class.
     */
    public static final Path TEMPORARY_RESULT_DIRECTORY_NAME = Path.of("target", "testing-directory-temporary-result");
//
//    /**
//     * Base path to the created plagiarism and the main file located in the project resources.
//     */
//    public static final Path BASE_PATH_TO_JAVA_RESOURCES_SORTALGO = Path.of("src", "test", "resources", "java", "sortAlgo");
//    
//    
//    
//    
// 
//    
    /**
     * Base path to the saved results
     */
    public static final Path BASE_PATH_TO_RESULT_JSON = Path.of("src", "test", "resources", "results");
//    /**
//     * Base path to the saved results of the previous tests in a *.json file for java
//     */
//    public static final Path BASE_PATH_TO_JAVA_RESULT_JSON = Path.of(BASE_PATH_TO_RESULT_JSON.toString(), "JavaResult.json");
//    
    
    
    
    
    
    public static final Path BASE_PATH_TO_LANGUAGE_RESOURCES = Path.of("src", "test", "resources");
    

  /**
   * Create the complete path to the submission files. Here the temporary system path is extended with the
   * "SUBMISSION_DIRECTORY_NAME", which is predefined in this class.
   */
  public static final String TEMPORARY_SUBMISSION_DIRECTORY_NAME = Path.of("target", "testing-directory-submission").toString();
}
