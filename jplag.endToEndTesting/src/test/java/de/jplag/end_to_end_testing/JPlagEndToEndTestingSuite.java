package de.jplag.end_to_end_testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.naming.NameNotFoundException;
import javax.naming.spi.DirStateFactory.Result;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestFactory;

import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.end_to_end_testing.constants.TestDirectoryConstants;
import de.jplag.end_to_end_testing.helper.FileHelper;
import de.jplag.end_to_end_testing.helper.JPlagTestSuiteHelper;
import de.jplag.end_to_end_testing.helper.JsonHelper;
import de.jplag.end_to_end_testing.model.ExpectedResult;
import de.jplag.end_to_end_testing.model.Options;
import de.jplag.end_to_end_testing.model.ResultDescription;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;

public class JPlagEndToEndTestingSuite {
	// Language -> direcotry names and Paths
	private Map<LanguageOption, Map<String, Path>> LanguageToTestCaseMapper;
	private List<Options> options;

	private static Map<String, List<ResultDescription>> temporaryResultList;
	// private static Map<String, ResultDescription> temporaryResultMap;
	// private static Map<String, List<Options , ExpectedResult>>
	// temporaryResultMap;

	public JPlagEndToEndTestingSuite() throws IOException {
		LanguageToTestCaseMapper = JPlagTestSuiteHelper.getAllLanguageResources();
		temporaryResultList = new HashMap<>();

		options = new ArrayList<>();
		options.add(new Options(1));
		options.add(new Options(15));

		// var test =
		// JsonHelper.getJsonModelListFromPath(Path.of(TestDirectoryConstants.BASE_PATH_TO_RESULT_JSON.toString()
		// , "JavaResult.json"));
	}

	@AfterAll
	public static void tearDown() throws IOException {
		for (var test : temporaryResultList.entrySet()) {
			JsonHelper.writeJsonModelsToJsonFile(test.getValue(), test.getKey());
		}
		var test = "";
	}
//	@AfterAll
//	public static void tearDown() throws IOException {
//		for(Entry<String, ResultDescription> temporaryRestultElement : temporaryResultMap.entrySet())
//		{
//			var test = temporaryResultMap.get(temporaryRestultElement.getKey());
//			JsonHelper.writeToJsonFile(temporaryRestultElement.getValue(), temporaryRestultElement.getKey());	
//		}
//		var tes = "";
//	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	@TestFactory
	Collection<DynamicTest> dynamicOverAllTest() throws IOException {
		for (Entry<LanguageOption, Map<String, Path>> languageMap : LanguageToTestCaseMapper.entrySet()) {
			LanguageOption currentLanguageOption = languageMap.getKey();
			for (Entry<String, Path> languagePaths : languageMap.getValue().entrySet()) {
				String[] fileNames = FileHelper.loadAllTestFileNames(languagePaths.getValue());
				var testCases = JPlagTestSuiteHelper.getPermutation(fileNames, languagePaths.getValue());
				var testCollection = new ArrayList<DynamicTest>();
				String directoryName = languagePaths.getValue().getFileName().toString();
				List<ResultDescription> tempResult = JsonHelper.getJsonModelListFromPath(directoryName, currentLanguageOption);
				for (Options option : options) {
					for (var testCase : testCases) {
						Optional<ResultDescription> currentResultDescription = tempResult.stream()
								.filter(x -> x.getOptions().equals(option)).findFirst();
						
						testCollection.add(DynamicTest
								.dynamicTest(getTestCaseDisplayName(option, currentLanguageOption, testCase), () -> {
									try {
										ResultDescription currentResult = currentResultDescription.isPresent() ? 
												currentResultDescription.get() : null;
										runJPlagTestSuite(directoryName, option, currentLanguageOption, testCase,
												currentResult);
									} finally {
										JPlagTestSuiteHelper.clear();
									}
								}));
					}
				}
				return testCollection;
			}
		}
		return null;
	}


	private void runJPlagTestSuite(String directoryName, Options options, LanguageOption languageOption,
			String[] testFiles, ResultDescription currentResultDescription)
			throws ExitException, NoSuchAlgorithmException, NameNotFoundException, IOException {
		String[] submissionPath = FileHelper.createNewTestCaseDirectory(testFiles);

		JPlagOptions jplagOptions = new JPlagOptions(Arrays.asList(submissionPath), new ArrayList<>(), languageOption);

		jplagOptions.setMinimumTokenMatch(options.getMinimumTokenMatch());

		JPlagResult jplagResult = new JPlag(jplagOptions).run();
		Options currentOption = new Options(jplagOptions);

		List<JPlagComparison> currentJPlagComparison = jplagResult.getAllComparisons();

		// jplagTestSuiteHelper.saveTemporaryResult(currentJPlagComparison,testCaseModel.getJPlagOptionsFromCurrentModel()
		// ,testCaseModel.getFunctionName());

		for (JPlagComparison jPlagComparison : currentJPlagComparison) {
			String identifier = JPlagTestSuiteHelper.getTestIdentifier(jPlagComparison);
			addToTemporaryResultMap(directoryName, options, jPlagComparison, languageOption);
			assertNotNull(currentResultDescription , "No stored result could be found for the current LanguageOption! " + options.toString());
			var result = currentResultDescription
					.getExpectedResultByIdentifier(JPlagTestSuiteHelper.getTestIdentifier(jPlagComparison));

			assertNotNull(result, "No stored result could be found for the identifier! " + identifier);
			assertEquals(result.getResultSimilarityMinimum(), jPlagComparison.minimalSimilarity(),
					"The JPlag results [minimalSimilarity] do not match the stored values!");
			assertEquals(result.getResultSimilarityMaximum(), jPlagComparison.maximalSimilarity(),
					"The JPlag results [maximalSimilarity] do not match the stored values!");
			assertEquals(result.getResultMatchedTokenNumber(), jPlagComparison.getNumberOfMatchedTokens(),
					"The JPlag results [numberOfMatchedTokens] do not match the stored values!");
		}
		assertEquals(1, 1);
	}

	private void addToTemporaryResultMap(String directoryName, Options options, JPlagComparison jPlagComparison,
			LanguageOption languageOption) {
		var element = temporaryResultList.get(directoryName);
		if (element != null) {
			for (var item : element) {
				if (item.getOptions().equals(options)) {
					item.putIdenfifierToResultMap(JPlagTestSuiteHelper.getTestIdentifier(jPlagComparison),
							new ExpectedResult(jPlagComparison));
					return;
				}

			}
			element.add(new ResultDescription(options, jPlagComparison, languageOption));
//			element.get(0).putIdenfifierToResultMap(directoryName, new ExpectedResult(jPlagComparison));
//			element.add(new ResultDescription(options, jPlagComparison, languageOption));
		} else {
			var temporaryNewResultList = new ArrayList<ResultDescription>();
			temporaryNewResultList.add(new ResultDescription(options, jPlagComparison, languageOption));
			temporaryResultList.put(directoryName, temporaryNewResultList);
		}
	}

	/**
	 * 
	 * @param languageOption
	 * @param testFiles
	 * @return
	 */
	private String getTestCaseDisplayName(Options option, LanguageOption languageOption, String[] testFiles) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("(" + String.valueOf(option.getMinimumTokenMatch()) + ")");
		for (int counter = 0; counter < testFiles.length; counter++) {
			String fileName = Path.of(testFiles[counter]).toFile().getName();
			stringBuilder.append(fileName.substring(0, fileName.lastIndexOf('.')));
			if (counter + 1 < testFiles.length) {
				stringBuilder.append("_");
			}

		}

		return languageOption.toString() + ": " + stringBuilder.toString();
	}
}
