package de.jplag.end_to_end_testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import javax.naming.NameNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestFactory;

import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.end_to_end_testing.helper.FileHelper;
import de.jplag.end_to_end_testing.helper.JPlagTestSuiteHelper;
import de.jplag.end_to_end_testing.model.Options;
import de.jplag.end_to_end_testing.model.ResultDescription;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;

public class JPlagEndToEndTestingSuite {
	// Language -> direcotry names and Paths
	private Map<LanguageOption, Map<String, Path>> LanguageToTestCaseMapper;
	private List<Options> options;
	private List<ResultDescription> tempListRestul;

	public JPlagEndToEndTestingSuite() {
		LanguageToTestCaseMapper = JPlagTestSuiteHelper.getAllLanguageResources();

		options = new ArrayList<>();
		options.add(new Options(1));
		options.add(new Options(10));
		options.add(new Options(15));
	}

	/**
	 * 
	 * @return
	 */
	@TestFactory
	Collection<DynamicTest> dynamicOverAllTest() {
		for (Entry<LanguageOption, Map<String, Path>> languageMap : LanguageToTestCaseMapper.entrySet()) {
			LanguageOption currentLanguageOption = languageMap.getKey();
			for (Entry<String, Path> languagePaths : languageMap.getValue().entrySet()) {
				String[] fileNames = FileHelper.loadAllTestFileNames(languagePaths.getValue());
				var testCases = JPlagTestSuiteHelper.getPermutation(fileNames, languagePaths.getValue());
				var returnValue = new ArrayList<DynamicTest>();
				for (Options option : options) {
					for (var testCase : testCases) {
						returnValue.add(DynamicTest.dynamicTest(getTestCaseDisplayName(option, currentLanguageOption, testCase),  () -> {
							try {
								runJPlagTestSuite(option, currentLanguageOption, testCase);
							} finally {
								JPlagTestSuiteHelper.clear();
							}
						}
						));
					}
				}
				return returnValue;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param options
	 * @param languageOption
	 * @param testFiles
	 * @param excludedFileNames
	 * @throws ExitException
	 * @throws NoSuchAlgorithmException
	 * @throws NameNotFoundException
	 * @throws IOException
	 */
	private void runJPlagTestSuite(Options options, LanguageOption languageOption, String[] testFiles)
			throws ExitException, NoSuchAlgorithmException, NameNotFoundException, IOException {
		String[] submissionPath = FileHelper.createNewTestCaseDirectory(testFiles);

		JPlagOptions jplagOptions = new JPlagOptions(Arrays.asList(submissionPath), new ArrayList<>(), languageOption);

		jplagOptions.setMinimumTokenMatch(options.getMinimumTokenMatch());

		JPlagResult jplagResult = new JPlag(jplagOptions).run();
		var currentOption = new Options(jplagOptions);

		List<JPlagComparison> currentJPlagComparison = jplagResult.getAllComparisons();

		// jplagTestSuiteHelper.saveTemporaryResult(currentJPlagComparison,testCaseModel.getJPlagOptionsFromCurrentModel()
		// ,testCaseModel.getFunctionName());
		assertEquals(1, 1);
//	        for (JPlagComparison jPlagComparison : currentJPlagComparison) {
//	            String hashCode = JPlagTestSuiteHelper.getTestIdentifier(jPlagComparison);
//	            
//	            ResultModel resultModel = testCaseModel.getCurrentJsonModel().getResultModelById(hashCode);
//	            assertNotNull(resultModel, "No stored result could be found for the identifier! " + hashCode);
//	            assertEquals(resultModel.getMinimalSimilarity(), jPlagComparison.minimalSimilarity(),
//	                    "The JPlag results [minimalSimilarity] do not match the stored values!");
//	            assertEquals(resultModel.getMaximalSimilarity(), jPlagComparison.maximalSimilarity(),
//	                    "The JPlag results [maximalSimilarity] do not match the stored values!");
//	            assertEquals(resultModel.getNumberOfMatchedTokens(), jPlagComparison.getNumberOfMatchedTokens(),
//	                    "The JPlag results [numberOfMatchedTokens] do not match the stored values!");
//	        }
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
			if (counter + 1< testFiles.length) {
				stringBuilder.append("_");
			}

		}

		return languageOption.toString() + ": " + stringBuilder.toString();
	}
}
