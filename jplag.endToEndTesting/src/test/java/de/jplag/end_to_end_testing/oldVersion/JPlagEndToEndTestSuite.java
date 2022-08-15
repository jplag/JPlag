//package de.jplag.end_to_end_testing.oldVersion;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.io.IOException;
//import java.security.NoSuchAlgorithmException;
//import java.util.List;
//
//import javax.naming.NameNotFoundException;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
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
//public class JPlagEndToEndTestSuite {
//    private JPlagTestSuiteHelper jplagTestSuiteHelper;
//
//    public JPlagTestSuiteHelper getJPlagTestSuiteHelper()
//    {
//    	return jplagTestSuiteHelper;
//    }
//    
//    public JPlagEndToEndTestSuite() throws IOException
//    {
//    	 jplagTestSuiteHelper = new JPlagTestSuiteHelper(LanguageOption.JAVA);
//         assertTrue(TestDirectoryConstants.BASE_PATH_TO_JAVA_RESOURCES_SORTALGO.toFile().exists(), "Could not find base directory!");
//         assertTrue(jplagTestSuiteHelper.getResultJsonPath().toFile().isFile(), "Could not find result json for the specified language!");
//    }
//    
//	 /**
//     * 
//     * @param testClassNames
//     * @param functionName
//     * @param jPlagOptions 
//     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
//     * @throws NoSuchAlgorithmException when no hash algorithm could be found
//     * @throws NameNotFoundException  if the no filenames cloud be found in the JPlagCOmparison object
//     * @throws IOException  is thrown in case of problems with copying the plagiarism classes
//     */
//    public void runJPlagTestSuite(String[] testClassNames, String functionName, JPlagOptions jPlagOptions) throws IOException, NoSuchAlgorithmException, NameNotFoundException, ExitException
//    {
//    	TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames, functionName);
//        testCaseModel.setJPlagOptions(jPlagOptions);
//        runJPlagTestSuite(testCaseModel);
//    }
//    
//    /**
//     * 
//     * @param testClassNames
//     * @param functionName
//     * @param minimumTokenMatch 
//     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
//     * @throws NoSuchAlgorithmException when no hash algorithm could be found
//     * @throws NameNotFoundException  if the no filenames cloud be found in the JPlagCOmparison object
//     * @throws IOException  is thrown in case of problems with copying the plagiarism classes
//     */
//    public void runJPlagTestSuite(String[] testClassNames, String functionName, int minimumTokenMatch) throws IOException, NoSuchAlgorithmException, NameNotFoundException, ExitException
//    {
//    	TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames, functionName);
//        testCaseModel.setMinimumTokenMatch(minimumTokenMatch);
//        runJPlagTestSuite(testCaseModel);
//    }
//   
//    /**
//     * 
//     * @param testClassNames
//     * @param functionName
//     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
//     * @throws NoSuchAlgorithmException when no hash algorithm could be found
//     * @throws NameNotFoundException  if the no filenames cloud be found in the JPlagCOmparison object
//     * @throws IOException  is thrown in case of problems with copying the plagiarism classes
//     */
//    public void runJPlagTestSuite(String[] testClassNames, String functionName)
//            throws IOException, ExitException, NoSuchAlgorithmException, NameNotFoundException {
//        TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames, functionName);
//        runJPlagTestSuite(testCaseModel);
//    }
//    
//    /**
//     * This method creates the necessary results as well as models for a test run and summarizes them for a comparison.
//     * @param testCaseModel
//     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
//     * @throws NoSuchAlgorithmException when no hash algorithm could be found
//     * @throws NameNotFoundException  if the no filenames cloud be found in the JPlagCOmparison object
//     * @throws IOException  is thrown in case of problems with copying the plagiarism classes
//     */
//    private void runJPlagTestSuite(TestCaseModel testCaseModel) throws ExitException, NoSuchAlgorithmException, NameNotFoundException, IOException
//    {
//    	try {
//    	JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();
//        List<JPlagComparison> currentJPlagComparison = jplagResult.getAllComparisons();
//        //jplagTestSuiteHelper.saveTemporaryResult(currentJPlagComparison,testCaseModel.getJPlagOptionsFromCurrentModel() ,testCaseModel.getFunctionName());
//
//        for (JPlagComparison jPlagComparison : currentJPlagComparison) {
//            String hashCode = jplagTestSuiteHelper.getTestIdentifier(jPlagComparison);
//            ResultModel resultModel = testCaseModel.getCurrentJsonModel().getResultModelById(hashCode);
//            assertNotNull(resultModel, "No stored result could be found for the identifier! " + hashCode);
//            assertEquals(resultModel.getMinimalSimilarity(), jPlagComparison.minimalSimilarity(),
//                    "The JPlag results [minimalSimilarity] do not match the stored values!");
//            assertEquals(resultModel.getMaximalSimilarity(), jPlagComparison.maximalSimilarity(),
//                    "The JPlag results [maximalSimilarity] do not match the stored values!");
//            assertEquals(resultModel.getNumberOfMatchedTokens(), jPlagComparison.getNumberOfMatchedTokens(),
//                    "The JPlag results [numberOfMatchedTokens] do not match the stored values!");
//        }
//    	}
//    	finally {
//    		jplagTestSuiteHelper.clear();
//		}
//    }
//}
