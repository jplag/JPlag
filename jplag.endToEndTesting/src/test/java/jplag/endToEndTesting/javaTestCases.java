package jplag.endToEndTesting;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.endToEndTesting.constants.Constant;
import de.jplag.endToEndTesting.helper.JPlagTestSuiteHelper;
import de.jplag.endToEndTesting.helper.ResultComparison;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;

class javaTestCases {

	@Before
	public void setUp() {
		assertTrue(Constant.BASE_PATH_TO_JAVA_RESOURCES_SORTALGO.toFile().exists(), "Could not find base directory!");
		try {
			assertTrue(Constant.BASE_PATH_TO_JAVA_RESULT_JSON.toFile().exists(),
					"Could not find java result xml at " + Constant.BASE_PATH_TO_JAVA_RESULT_JSON + "!");

		} catch (Exception e) {
			assertTrue(false, e.getMessage());
		}
	}

	/**
	 * Inserting comments or empty lines (normalization level)
	 */
	@Test
	void normalizationLevelTest_one() {

		String[] testClassNames = new String[] { "SortAlgo.java", "SortAlgo1.java" };

		try {
			var testSuiteHelper = new JPlagTestSuiteHelper(testClassNames);

			List<String> submissionTestPaths = new ArrayList<String>();
			submissionTestPaths.add(testSuiteHelper.getFolderPath());

			JPlagOptions options = new JPlagOptions(submissionTestPaths, new ArrayList<String>(), LanguageOption.JAVA);

			JPlag jplag = new JPlag(options);
			JPlagResult result = jplag.run();
			// after close the created directories are deleted
			testSuiteHelper.close();

			ResultComparison resultComparison = new ResultComparison(new Object() {
			}.getClass().getEnclosingMethod().getName());

			// for the fine-granular testing strategy, the result object contains only one
			// comparison.
			for (JPlagComparison jPlagComparison : result.getAllComparisons()) {
				System.out.println("Comparison of the stored values and the current equality values");
				assertEquals(resultComparison.getSimilarity(), jPlagComparison.similarity());
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Changing variable names or function names (normalization level)
	 */
	@Test
	void normalizationLevelTest_two() {
		String[] testClassNames = new String[] { "SortAlgo.java", "SortAlgo2.java" };

		try {
			var testSuiteHelper = new JPlagTestSuiteHelper(testClassNames);

			List<String> tempList = new ArrayList<String>();
			tempList.add(testSuiteHelper.getFolderPath());

			JPlagOptions options = new JPlagOptions(tempList, new ArrayList<String>(), LanguageOption.JAVA);

			JPlag jplag = new JPlag(options);
			JPlagResult result = jplag.run();
			// after close the created directories are deleted
			testSuiteHelper.close();

			ResultComparison resultComparison = new ResultComparison(new Object() {
			}.getClass().getEnclosingMethod().getName());
			// for the fine-granular testing strategy, the result object contains only one
			// comparison.
			for (JPlagComparison jPlagComparison : result.getAllComparisons()) {
				System.out.println("Comparison of the stored values and the current equality values");
				assertEquals(resultComparison.getSimilarity(), jPlagComparison.similarity());
			}
			// after close the created directories are deleted

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Inserting comments or empty lines (normalization level)
	 */
	@Test
	void normalizationLevelTest_three() {
		String[] testClassNames = new String[] { "SortAlgo1.java", "SortAlgo2.java" };

		try {
			var testSuiteHelper = new JPlagTestSuiteHelper(testClassNames);

			List<String> tempList = new ArrayList<String>();
			tempList.add(testSuiteHelper.getFolderPath());

			JPlagOptions options = new JPlagOptions(tempList, new ArrayList<String>(), LanguageOption.JAVA);

			JPlag jplag = new JPlag(options);
			JPlagResult result = jplag.run();
			// after close the created directories are deleted
			testSuiteHelper.close();

			ResultComparison resultComparison = new ResultComparison(new Object() {
			}.getClass().getEnclosingMethod().getName());
			// for the fine-granular testing strategy, the result object contains only one
			// comparison.
			for (JPlagComparison jPlagComparison : result.getAllComparisons()) {
				System.out.println("Comparison of the stored values and the current equality values");
				assertEquals(resultComparison.getSimilarity(), jPlagComparison.similarity());
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Insertion of unnecessary or changed code lines (token generation)
	 */
	@Test
	void tokenGenerationLevelTest_one() {
		String[] testClassNames = new String[] { "SortAlgo.java", "SortAlgo3.java" };

		try {
			var testSuiteHelper = new JPlagTestSuiteHelper(testClassNames);

			List<String> tempList = new ArrayList<String>();
			tempList.add(testSuiteHelper.getFolderPath());

			JPlagOptions options = new JPlagOptions(tempList, new ArrayList<String>(), LanguageOption.JAVA);

			JPlag jplag = new JPlag(options);
			JPlagResult result = jplag.run();
			// after close the created directories are deleted
			testSuiteHelper.close();

			ResultComparison resultComparison = new ResultComparison(new Object() {
			}.getClass().getEnclosingMethod().getName());
			// for the fine-granular testing strategy, the result object contains only one
			// comparison.
			for (JPlagComparison jPlagComparison : result.getAllComparisons()) {
				System.out.println("Comparison of the stored values and the current equality values");
				assertEquals(resultComparison.getSimilarity(), jPlagComparison.similarity());
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Changing the program flow (token generation) (statments and functions must be
	 * independent from each other)
	 */
	@Test
	void tokenGenerationLevelTest_two() {
		String[] testClassNames = new String[] { "SortAlgo.java", "SortAlgo4.java" };

		try {
			var testSuiteHelper = new JPlagTestSuiteHelper(testClassNames);

			List<String> tempList = new ArrayList<String>();
			tempList.add(testSuiteHelper.getFolderPath());

			JPlagOptions options = new JPlagOptions(tempList, new ArrayList<String>(), LanguageOption.JAVA);

			JPlag jplag = new JPlag(options);
			JPlagResult result = jplag.run();
			// after close the created directories are deleted
			testSuiteHelper.close();

			ResultComparison resultComparison = new ResultComparison(new Object() {
			}.getClass().getEnclosingMethod().getName());
			// for the fine-granular testing strategy, the result object contains only one
			// comparison.
			for (JPlagComparison jPlagComparison : result.getAllComparisons()) {
				System.out.println("Comparison of the stored values and the current equality values");
				assertEquals(resultComparison.getSimilarity(), jPlagComparison.similarity());
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Variable decleration at the beginning of the program (Detecting Source Code
	 * Plagiarism [...])
	 */
	@Test
	void tokenGenerationLevelTest_three() {
		String[] testClassNames = new String[] { "SortAlgo.java", "SortAlgo4d1.java" };

		try {
			var testSuiteHelper = new JPlagTestSuiteHelper(testClassNames);

			List<String> tempList = new ArrayList<String>();
			tempList.add(testSuiteHelper.getFolderPath());

			JPlagOptions options = new JPlagOptions(tempList, new ArrayList<String>(), LanguageOption.JAVA);

			JPlag jplag = new JPlag(options);
			JPlagResult result = jplag.run();
			// after close the created directories are deleted
			testSuiteHelper.close();

			ResultComparison resultComparison = new ResultComparison(new Object() {
			}.getClass().getEnclosingMethod().getName());
			// for the fine-granular testing strategy, the result object contains only one
			// comparison.
			for (JPlagComparison jPlagComparison : result.getAllComparisons()) {
				System.out.println("Comparison of the stored values and the current equality values");
				assertEquals(resultComparison.getSimilarity(), jPlagComparison.similarity());
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
