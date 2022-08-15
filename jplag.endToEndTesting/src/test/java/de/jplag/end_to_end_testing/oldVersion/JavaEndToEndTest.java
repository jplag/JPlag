//package de.jplag.end_to_end_testing.oldVersion;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.io.IOException;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Stream;
//
//import javax.naming.NameNotFoundException;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DynamicTest;
//import org.junit.jupiter.api.TestFactory;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.junit.jupiter.params.provider.ValueSource;
//
//import de.jplag.JPlag;
//import de.jplag.JPlagComparison;
//import de.jplag.JPlagResult;
//import de.jplag.end_to_end_testing.constants.TestDirectoryConstants;
//import de.jplag.end_to_end_testing.helper.JPlagTestSuiteHelper;
//import de.jplag.end_to_end_testing.model.ResultModel;
//import de.jplag.end_to_end_testing.model.TestCaseModel;
//import de.jplag.exceptions.ExitException;
//import de.jplag.options.JPlagOptions;
//import de.jplag.options.LanguageOption;
//
///**
// * Main test class for end-to-end testing in the Java language. The test cases
// * aim to detect changes in the detection of plagiarism in the Java language and
// * to be able to roughly categorize them. The plagiarism is compared with the
// * original class. The results are compared with the results from previous tests
// * and changes are detected.
// */
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class JavaEndToEndTest {
//
//	private JPlagEndToEndTestSuite jPlagEndToEndTestSuite;
//
//	@BeforeAll
//	public void setUp() throws IOException {
//		jPlagEndToEndTestSuite = new JPlagEndToEndTestSuite();
//	}
//
//	/**
//	 * Test cases created for the normalization level
//	 * 
//	 * @return the classes to be tested with the corresponding identifier
//	 */
//	private static Stream<Arguments> normalizationLevelTestArguments() {
//		return Stream.of(Arguments.of((Object) new String[] { "SortAlgo.java", "SortAlgo1.java" }),
//				Arguments.of((Object) new String[] { "SortAlgo.java", "SortAlgo2.java" }),
//				Arguments.of((Object) new String[] { "SortAlgo1.java", "SortAlgo2.java" }));
//	}
//
//	/**
//	 * Test cases for the token generation level
//	 * 
//	 * @return the classes to be tested with the corresponding identifier
//	 */
//	private static Stream<Arguments> tokenGenerationLevelTestArguments() {
//		return Stream.of(Arguments.of((Object) new String[] { "SortAlgo.java", "SortAlgo3.java" }),
//				Arguments.of((Object) new String[] { "SortAlgo.java", "SortAlgo4.java" }),
//				Arguments.of((Object) new String[] { "SortAlgo.java", "SortAlgo4d1.java" }));
//	}
//
//	/**
//	 * Inserting comments or empty lines (normalization level) -> id = 0 Changing
//	 * variable names or function names (normalization level) -> id = 1 Inserting
//	 * comments or empty lines (normalization level) -> id = 2
//	 * 
//	 * @param testClassNames Plagiarized classes names in the resource directorie
//	 *                       which are needed for the test
//	 * @throws IOException              is thrown in case of problems with copying
//	 *                                  the plagiarism classes
//	 * @throws ExitException            in case the plagiarism detection with JPlag
//	 *                                  is preemptively terminated would be of the
//	 *                                  test.
//	 * @throws NoSuchAlgorithmException when no hash algorithm could be found
//	 * @throws NameNotFoundException    if the no filenames cloud be found in the
//	 *                                  JPlagCOmparison object
//	 */
//	@ParameterizedTest
//	@MethodSource("normalizationLevelTestArguments")
//	void normalizationLevelTest(String[] testClassNames)
//			throws IOException, ExitException, NoSuchAlgorithmException, NameNotFoundException {
//		jPlagEndToEndTestSuite.runJPlagTestSuite(testClassNames, new Object() {
//		}.getClass().getEnclosingMethod().getName());
//	}
//
//	/**
//	 * Insertion of unnecessary or changed code lines (token generation) -> id = 0
//	 * Changing the program flow (token generation) (statements and functions must
//	 * be independent from each other) -> id = 1 Variable declaration at the
//	 * beginning of the program (Detecting Source Code Plagiarism [...]) -> id = 2
//	 * 
//	 * @param testClassNames Plagiarized classes names in the resource directorie
//	 *                       which are needed for the test
//	 * @throws IOException              is thrown in case of problems with copying
//	 *                                  the plagiarism classes
//	 * @throws ExitException            in case the plagiarism detection with JPlag
//	 *                                  is preemptively terminated would be of the
//	 *                                  test.
//	 * @throws NoSuchAlgorithmException when no hash algorithm could be found
//	 * @throws NameNotFound             if the no filenames cloud be found in the
//	 *                                  JPlagCOmparison object
//	 */
//	@ParameterizedTest
//	@MethodSource("tokenGenerationLevelTestArguments")
//	void tokenGenerationLevelTest(String[] testClassNames)
//			throws IOException, ExitException, NoSuchAlgorithmException, NameNotFoundException {
//		jPlagEndToEndTestSuite.runJPlagTestSuite(testClassNames, new Object() {
//		}.getClass().getEnclosingMethod().getName());
//	}
//
//	/**
//	 * In this test case, all test cases contained for the language are tested
//	 * against each other and compared. This happens at runtime and is made possible
//	 * by DynamicTest streams.
//	 * 
//	 * @return A DynamicTest is a test case generated at runtime.
//	 */
//	@TestFactory
//	Stream<DynamicTest> dynamicOverAllTest() {
//		// test cases calculated with (n over k)
//		JPlagTestSuiteHelper jplagTestSuiteHelper = jPlagEndToEndTestSuite.getJPlagTestSuiteHelper();
//		var fileNames = jplagTestSuiteHelper.getAllTestFileNames();
//		ArrayList<String[]> testCases = new ArrayList<>();
//		int outerCounter = 1;
//		for (String fileName : fileNames) {
//			for (int counter = outerCounter; counter < fileNames.length; counter++) {
//				testCases.add(new String[] { fileName, fileNames[counter] });
//			}
//			outerCounter++;
//		}
//		String functionName = new Object() {
//		}.getClass().getEnclosingMethod().getName();
//
//		return testCases.stream()
//				.map(testCase -> DynamicTest.dynamicTest("Testing: " + testCase[0] + " " + testCase[1], () -> {
//
//					jPlagEndToEndTestSuite.runJPlagTestSuite(testCase, functionName);
//				}));
//	}
//}
