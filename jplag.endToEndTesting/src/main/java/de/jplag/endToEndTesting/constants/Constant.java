package de.jplag.endToEndTesting.constants;

import java.nio.file.Path;

public final class Constant {

	private Constant() {
		// private constructor to prevent instantiation
	}

	// can be exchanged for a suitable standard path if necessary
	public static final String EMPTY_STRING = "";
	public static final String TEMPORARY_DIRECTORY_NAME = "submission";
	public static final String TEMPORARY_SYSTEM_DIRECTORY = "java.io.tmpdir";
	
	public static final String TEMPORARY_SUBMISSION_DIRECTORY_NAME = Path.of(System.getProperty(TEMPORARY_SYSTEM_DIRECTORY), TEMPORARY_DIRECTORY_NAME).toString();

	// constant final project strings
	public static final Path BASE_PATH_TO_JAVA_RESOURCES_SORTALGO = Path.of("src", "test", "resources", "java",
			"sortAlgo");
	public static final Path BASE_PATH_TO_JAVA_RESULT_JSON = Path.of("src", "test", "resources", "results",
			"javaResult.json");
}
