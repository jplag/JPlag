package jplag.endToEndTesting;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.endToEndTesting.constants.Constant;
import de.jplag.endToEndTesting.helper.JPlagTestSuiteHelper;
import de.jplag.options.LanguageOption;

import model.TestCaseModel;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class javaTestCases {
	private JPlagTestSuiteHelper jplagTestSuiteHelper;

	@BeforeAll
	public void setUp() throws Exception {
		jplagTestSuiteHelper = new JPlagTestSuiteHelper(LanguageOption.JAVA);
		assertTrue(Constant.BASE_PATH_TO_JAVA_RESOURCES_SORTALGO.toFile().exists(), "Could not find base directory!");
		assertTrue(Constant.BASE_PATH_TO_JAVA_RESULT_JSON.toFile().exists(),
				"Could not find java result json at " + Constant.BASE_PATH_TO_JAVA_RESULT_JSON + "!");
	}

	@AfterEach
	public void teardown() throws Exception {
		// after close the created directories are deleted
		jplagTestSuiteHelper.clear();
	}

	/**
	 * Inserting comments or empty lines (normalization level)
	 * 
	 * @throws Exception
	 */
	@Test
	void normalizationLevelTest_one() throws Exception {

		String[] testClassNames = new String[] { "SortAlgo.java", "SortAlgo1.java" };

		TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

		JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

		assertTrue(testCaseModel.compaireModelProperties(jplagResult), "dose not match");
	}

	/**
	 * Changing variable names or function names (normalization level)
	 * 
	 * @throws Exception
	 */
	@Test
	void normalizationLevelTest_two() throws Exception {
		String[] testClassNames = new String[] { "SortAlgo.java", "SortAlgo2.java" };

		TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

		JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

		assertTrue(testCaseModel.compaireModelProperties(jplagResult), "dose not match");
	}

	/**
	 * Inserting comments or empty lines (normalization level)
	 * 
	 * @throws Exception
	 */
	@Test
	void normalizationLevelTest_three() throws Exception {
		String[] testClassNames = new String[] { "SortAlgo1.java", "SortAlgo2.java" };

		TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

		JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

		assertTrue(testCaseModel.compaireModelProperties(jplagResult), "dose not match");
	}

	/**
	 * Insertion of unnecessary or changed code lines (token generation)
	 * 
	 * @throws Exception
	 */
	@Test
	void tokenGenerationLevelTest_one() throws Exception {
		String[] testClassNames = new String[] { "SortAlgo.java", "SortAlgo3.java" };

		TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

		JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

		assertTrue(testCaseModel.compaireModelProperties(jplagResult), "dose not match");
	}

	/**
	 * Changing the program flow (token generation) (statments and functions must be
	 * independent from each other)
	 * 
	 * @throws Exception
	 */
	@Test
	void tokenGenerationLevelTest_two() throws Exception {
		String[] testClassNames = new String[] { "SortAlgo.java", "SortAlgo4.java" };

		TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

		JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

		assertTrue(testCaseModel.compaireModelProperties(jplagResult), "dose not match");
	}

	/**
	 * Variable decleration at the beginning of the program (Detecting Source Code
	 * Plagiarism [...])
	 * @throws Exception 
	 */
	@Test
	void tokenGenerationLevelTest_three() throws Exception {
		String[] testClassNames = new String[] { "SortAlgo.java", "SortAlgo4d1.java" };

		TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames);

		JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

		assertTrue(testCaseModel.compaireModelProperties(jplagResult), "dose not match");
	}
}
