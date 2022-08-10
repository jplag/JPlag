# JPlag - End To End Testing
With the help of the end-to-end module, changes to the detection of JPlag are to be tested.
With the help of elaborated plagiarisms, which have been worked out from suggestions in the literature on the topic of "plagiarism detection and avoidance", a wide range of detectable change can be covered. The selected plagiarisms are the decisive factor here as to whether a change in recognition can be perceived. 

## References
These elaborations provide basic ideas on how a modification of the plagiarized source code can look like or be adapted.
These code adaptations refer to a wide range of changes starting from
adding/removing comments to architectural changes in the deliverables.

The following elaborations were used to be able to create the plagiarisms with the largest coverage:
- [Mossad: defeating software plagiarism detection](https://dl.acm.org/doi/abs/10.1145/3428206 "Mossad: defeating software plagiarism detection")
- [Detecting Source Code Plagiarism on Introductory Programming Course Assignments Using a Bytecode Approach - Oscar Karnalim](https://ieeexplore.ieee.org/abstract/document/7910274 "Detecting Source Code Plagiarism on Introductory Programming Course Assignments Using a Bytecode Approach - Oscar Karnalim")
- [Detecting Disguised Plagiarism - Hatem A. Mahmoud](https://arxiv.org/abs/1711.02149 "Detecting Disguised Plagiarism - Hatem A. Mahmoud")
- [Instructor-centric source code plagiarism detection and plagiarism corpus](https://dl.acm.org/doi/abs/10.1145/2325296.2325328 "Instructor-centric source code plagiarism detection and plagiarism corpus")

## Steps Towards Plagiarism
The following changes were applied to sample tasks to create test cases:
<ul type="1">
	<li>Inserting comments or empty lines (normalization level)</li>
	<li>Changing variable names or function names (normalization level)</li>
	<li>Insertion of unnecessary or changed code lines (token generation)</li>
	<li>Changing the program flow (token generation) (statments and functions must be independent from each other)</li>
		<ul>
			<li>Variable decleration at the beginning of the program</li>
			<li>Combining declerations of variables</li>
			<li>Reuse of the same variable for other functions</li>
		</ul>
	<li>Changing control structures</li>
		<ul>
			<li>for(...) to while(...)</li>
			<li>if(...) to switch-case</li>
		</ul>
	<li>Modification of expressions</li>
		<ul>
			<li>(X < Y) to !(X >= Y) and ++x to x = x + 1</li>
		</ul>
	<li>Splitting and merging statements</li>
		<ul>
			<li>x = getSomeValue(); y = x- z; to y = (getSomeValue() - Z;</li>
		</ul>
	<li>Inserting unnecessary casts</li>
</ul>

More detailed information about the create as well as about the subject to the issue can be found in the issue [Develop an end-to-end testing strategy](https://github.com/jplag/JPlag/issues/193 "Develop an end-to-end testing strategy").

**The changes listed above have been developed and evaluated for purely scientific purposes and are not intended to be used for plagiarism in the public or private domain.**

Software is according to [§ 2 of the copyright law](https://www.gesetze-im-internet.de/urhg/__2.html "§ 2 of the copyright law") a protected work which may not be plagiarized. 

## JPlag - End To End TestSuite Structure
The construction of an end to end test is done with the help of the JPlag api. 

``` java 
[...]
    /**
     * This method creates the necessary results as well as models for a test run and summarizes them for a comparison.
     * @param testClassNames Plagiarized classes names in the resource directorie which are needed for the test
     * @param testIdentifier name of the testId to load and identify the stored results
     * @throws IOException is thrown in case of problems with copying the plagiarism classes
     * @throws ExitException in case the plagiarism detection with JPlag is preemptively terminated would be of the test.
     */
    private void runJPlagTestSuite(String[] testClassNames, int testIdentifier) throws IOException, ExitException {
        String functionName = StackWalker.getInstance().walk(stream -> stream.skip(1).findFirst().get()).getMethodName();
        TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames, functionName);
        JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();

        for (JPlagComparison jPlagComparison : jplagResult.getAllComparisons()) {
            assertEquals(testCaseModel.getCurrentJsonModel().getResultModelById(testIdentifier).getResultSimilarity(), jPlagComparison.similarity(),
                    "The JPlag results [similarity] do not match the stored values!");
        }
    }
[...]
```
The created plagiarisms are copied with the JPlagTestSuiteHelper into temporary directories, which can then be checked with JPlag. 

In order to be able to distinguish in which domain of the recognition changes have occurred, fine granular test cases are used. These are composed of the changes already mentioned above. The plagiarism is compared with the original delivery and thus it is possible to detect and test small sections of the recognition. 

The comparative values were discussed and tested. The following results of the JPlag scan are used for the comparison:
1. similarity as `float`
2. minimal similarity as `float`
3. maximum similarity as `float`
4. matched token numbe as `int`

The comparative values were disscussed and elaborated in the issue [End to end testing - "comparative values"](https://github.com/jplag/JPlag/issues/548 "End to end testing - \"comparative values\""). 

The current JPlag scans will be compared with the stored ones.
This was done by storing the data in a *.json file which is read at the beginning of each test run. 

``` json  
[...]
{
  "function_name" : "normalizationLevelTest",
  "test_results" : [ {
    "result_similarity" : 100.0,
    "result_minimal_similarity" : 100.0,
    "result_maximum_similarity" : 100.0,
    "result_matched_token_number" : 56,
    "test_identifier" : "85FF00F531A497F002D40E9C8430CB159EFC40E618925FD757E35EB2A533227E"
  }, {
    "result_similarity" : 100.0,
    "result_minimal_similarity" : 100.0,
    "result_maximum_similarity" : 100.0,
    "result_matched_token_number" : 56,
    "test_identifier" : "0CA596F411FB0853C97216132D812A928A6A55CFAD5611A8B54857FF3E02D49F"
  }, {
    "result_similarity" : 100.0,
    "result_minimal_similarity" : 100.0,
    "result_maximum_similarity" : 100.0,
    "result_matched_token_number" : 56,
    "test_identifier" : "7F33B3C9DE2C754B30BBA5E19D864613585B233E7BB68F8B6AE32355BA301FC4"
  }
[...]
```
--- 

## Create New Language End To End Tests

This section explains how to create new end to end tests in the existing test suite. 
### Creating The Plagiarism
Before you add a new language to the end to end tests i would like to point out that the quality of the tests depends dreadfully on the plagiarism techniques you choose wicht were explaint in sechtion [Steps Towards Plagiarism](#steps-towards-plagiarism).
If you need more information about the creation of plans for this purpose, you can also read the elaborations that can be found under [References](#references).
The more and varied changes you apply, the more accurate the end-to-end tests for the language will be.

In the following an example is shown which is in the JavaEndToEnd tests and is used.

**Changing control structures for(…) to while(…):**

``` java
//base class
public class SortAlgo {
//[...]
public void BubbleSortWithoutRecursion(Integer arr[]) {
		for(int i = arr.length; i > 1 ; i--) {
			for(int innerCounter = 0; innerCounter < arr.length-1; innerCounter++)
			{
				if (arr[innerCounter] > arr[innerCounter + 1]) {
					swap(arr, innerCounter, (innerCounter + 1));
				}
			}
		}
	}
//[...]
}
```

``` java
//created plagiarism
public class SortAlgo5{
//[...]
public void BubbleSortWithoutRecursion(Integer arr[]) {
		int i = arr.length;
		while(i > 1)
		{
			int innerCounter = 0;
			while(innerCounter < arr.length -1)
			{
				if (arr[innerCounter] > arr[innerCounter + 1]) {
					swap(arr, innerCounter, (innerCounter + 1));
				}
				innerCounter++;
			}
			i--;
		}
	}
//[...]
}
```
### Copying Plagiarism To The Resources

The plagiarisms created in [Creating The Plagiarism](#creating-the-plagiarism) must now be copied to the corresponding resources folder. It is important not to mix the languages of the plagiarisms or to copy the data into bottle resource paths.

- At the path `JPlag\jplag.endToEndTesting\src\test\resources` a new folder for the language should be created if it does not already exist. For example `[...]\resources\java`. If you have plagiarized several different code samples, you can also create additional subfolders under the newly created folder for example `[...]\resources\java\sortAlgo`.

- Add the created path or paths to the [TestDirectoryConstants.java](https://github.com/jplag/JPlag/blob/master/jplag.endToEndTesting/src/main/java/de/jplag/end_to_end_testing/constants/TestDirectoryConstants.java) under `de.jplag.end_to_end_testing.constants/TestDirectoryConstants.java`. Please specify the subfolders, if any, as path. You can create and add as many folders as you like for the plagiarisms to be tested.

``` java 
[...]
/**
     * Base path to the created plagiarism and the main file located in the project resources.
     */
    public static final Path BASE_PATH_TO_YOUR-LANGUAGE_RESOURCES_YOUR-FOLDER = Path.of("src", "test", "resources", "YOUR-LANGUAGE", "YOUR-FOLDER");
[...]
```
- The thus created path must then be integrated into the existing [LanguageToPathMapper.java](https://github.com/jplag/JPlag/blob/master/jplag.endToEndTesting/src/main/java/de/jplag/end_to_end_testing/mapper/LanguageToPathMapper.java) at `JPlag\jplag.endToEndTesting\src\main\java\de\jplag\end_to_end_testing\mapper`

``` java
[...]
/**
     * @return  Mapper for the language-specific stored test plagiarism classes
     */
    private static final HashMap<LanguageOption, List<Path>> RESOURCE_PATH_MAPPER() {
        HashMap<LanguageOption, List<Path>> languageSpecificResultMapper = new HashMap<LanguageOption, List<Path>>();
        languageSpecificResultMapper.put(LanguageOption.JAVA,
		
        return languageSpecificResultMapper;
    }
[...]
```
- Create a new entry for this in the `RESOURCE_PATH_MAPPER`

``` java
languageSpecificResultMapper.put(LanguageOption.YOUR-LANGUAGE,
                Collections.unmodifiableList(Arrays.asList(TestDirectoryConstants.BASE_PATH_TO_YOUR-LANGUAGE_RESOURCES_YOUR-FOLDER)));
```

### Json Result Path

- The path to the result json file must also be created, even if it is empty during the first test run. For this they create a variable in the `TestDirectoryConstants.java ` classe as follows:

``` java
[...]
/**
     * Base path to the saved results of the previous tests in a *.json file for YOUR-LANGUAGE
     */
    public static final Path BASE_PATH_TO_YOUR-LANGUAGE_RESULT_JSON = Path.of(BASE_PATH_TO_RESULT_JSON.toString(), "YOUR-LANGUAGEResult.json");
[...]
```

- this path must be registered in the mapper that is responsible for the result paths `RESULT_PATH_MAPPER` in `de.jplag.end_to_end_testing.mapper.LanguageToPathMapper.java`

``` java
 languageSpecificResultMapper.put(LanguageOption.YOUR-LANGUAGE, TestDirectoryConstants.BASE_PATH_TO_YOUR-LANGUAGE_RESULT_JSON);
```

### Register The Temporary Result Path
- The new language must now be added to the `TEMPORARY_RESULT_PATH_MAPPER`.  This can be found in the `LanguageToPathMapper.java` also. 

``` java 
 languageSpecificResultMapper.put(LanguageOption.JAVA,
                Path.of(TestDirectoryConstants.TEMPORARY_RESULT_DIRECTORY_NAME.toString(), LanguageOption.YOUR-LANGUAGE.toString()));
```

### Create Save Test Function
In order to be able to save the current test results, another test must be created beforehand, which saves the temporary test results. 
These tests can be found in the `SaveTemporaryResults.java` class at `de.jplag.save_results_testcases`. 

``` java
[...]
  */
    @Disabled
    public void SaveYOUR-LANGUAGEResults() throws StreamReadException, DatabindException, IOException {
        insertNewTestResultsIntoJsonStore(LanguageOption.YOUR-LANGUAGE);
    }
[...]
```

It is important to leave the not needed languages on `@Disabled` to avoid unwanted changes. If you want to save the current results, set the test to `@Test` and run the class as `"JUnit Test"`.


### Write The Test Cases
After all of the previous steps have been completed, only the test class for the new language can be created. 

**Annotation:**
- Your newly added tests would be tested during the building of the JPlag scanner. It is important to make sure that the test cases are implemented correctly and cleanly. 
- The name of the new test case should end with "Test" so that this test can be recognized as a test in the build.
- If you have problems to build the test correctly you can have a look at the example in the [JavaEndToEndTest.java](https://github.com/jplag/JPlag/blob/master/jplag.endToEndTesting/src/test/java/de/jplag/end_to_end_testing/JavaEndToEndTest.java "example in the JavaEndToEndTest.java") class.  
- The test cases use [JUnit5](https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit-platform.html "JUnit5") and no other dependency of JUnit should be included in the project.

**Create New Test Class**
- Create a new test class under the path `en.jplag.end_to_end_testing`
- The name should be subject to the general naming convention and contain the language to be tested as well as ending with "Test". As an example `YOUR-LANGUAGEEndToEndTest`

The following example of a test class can be used to implement the first tests:

``` java 
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class YOUR-LANGUAGEEndToEndTest {
 private JPlagTestSuiteHelper jplagTestSuiteHelper;
     @BeforeAll
    public void setUp() throws IOException {
        jplagTestSuiteHelper = new JPlagTestSuiteHelper(LanguageOption.YOUR-LANGUAGE);
        assertTrue(TestDirectoryConstants.BASE_PATH_TO_YOUR-LANGUAGE_RESOURCES_SORTALGO.toFile().exists(), "Could not find base directory!");
        assertTrue(jplagTestSuiteHelper.getResultJsonPath().toFile().isFile(), "Could not find result json for the specified language!");
    }

    @AfterEach
    public void teardown() throws IOException {
        // after close the created directories are deleted
        jplagTestSuiteHelper.clear();
    }

    @Test
    void overAllTests() throws IOException, ExitException, NoSuchAlgorithmException {
        String[] testClassNames = jplagTestSuiteHelper.getAllTestFileNames();
        runJPlagTestSuite(testClassNames);
    }
	
	    private void runJPlagTestSuite(String[] testClassNames) throws IOException, ExitException, NoSuchAlgorithmException {
        String functionName = StackWalker.getInstance().walk(stream -> stream.skip(1).findFirst().get()).getMethodName();
        TestCaseModel testCaseModel = jplagTestSuiteHelper.createNewTestCase(testClassNames, functionName);
        JPlagResult jplagResult = new JPlag(testCaseModel.getJPlagOptionsFromCurrentModel()).run();
        List<JPlagComparison> currentJPlagComparison = jplagResult.getAllComparisons();
        jplagTestSuiteHelper.saveTemporaryResult(currentJPlagComparison, functionName);

        for (JPlagComparison jPlagComparison : currentJPlagComparison) {
            String hashCode = jplagTestSuiteHelper.getTestHashCode(jPlagComparison);
            ResultModel resultModel = testCaseModel.getCurrentJsonModel().getResultModelById(hashCode);
            assertNotNull(resultModel, "No stored result could be found for the identifier! " + hashCode);
            assertEquals(resultModel.getResultSimilarity(), jPlagComparison.similarity(),
                    "The JPlag results [similarity] do not match the stored values!");
            assertEquals(resultModel.getMinimalSimilarity(), jPlagComparison.minimalSimilarity(),
                    "The JPlag results [minimalSimilarity] do not match the stored values!");
            assertEquals(resultModel.getMaximalSimilarity(), jPlagComparison.maximalSimilarity(),
                    "The JPlag results [maximalSimilarity] do not match the stored values!");
            assertEquals(resultModel.getNumberOfMatchedTokens(), jPlagComparison.getNumberOfMatchedTokens(),
                    "The JPlag results [numberOfMatchedTokens] do not match the stored values!");
        }
    }
 }
```

- This sample class already contains the first test, which runs over all plagiarisms found in the specified resource paths. 
- When the test runs for the first time, the results are temporarily stored in `Path.of("target", "testing-directory-temporary-result");`. Also the test will fail the first time in any case, because there are no results yet with which the current results can be compared.
