package de.jplag.endToEndTesting.constants;

import java.nio.file.Path;

public final class Constant {

	private Constant() {
		// No need to instantiate the class, we can hide its constructor
	}

	// can be exchanged for a suitable standard path if necessary
	public static final String EMPTY_STRING = "";
	public static final String TEMP_DIRECTORY_NAME = "submission";
	public static final String TEMP_SYSTEM_DIRECTORY = "java.io.tmpdir";

	//constant final project strings
	public static final Path BASE_PATH_TO_JAVA_RESOURCES_SORTALGO = Path.of("src", "test", "resources", "java",
			"sortAlgo");
	public static final Path BASE_PATH_TO_JAVA_RESULT_JSON = Path.of("src", "test", "resources", "results",
			"javaResult.json");

	//json-file constants
	public static final String JSON_TEST_NAME_NODE = "functionName";
	public static final String JSON_SIMILARITY_NODE = "resultSimilarity";
}
