# JPlag - End To End Testing
With the help of the end-to-end module, changes to the detection of JPlag are to be tested.
With the help of elaborated plagiarisms, which have been worked out from suggestions in the literature on the topic of "plagiarism detection and avoidance", a wide range of detectable change can be covered. The selected plagiarisms are the decisive factor here as to whether a change in recognition can be perceived. 

### References
These elaborations provide basic ideas on how a modification of the plagiarized source code can look like or be adapted.
These code adaptations refer to a wide range of changes starting from
adding/removing comments to architectural changes in the deliverables.

The following elaborations were used to be able to create the plagiarisms with the largest coverage:
- [Mossad: defeating software plagiarism detection](https://dl.acm.org/doi/abs/10.1145/3428206 "Mossad: defeating software plagiarism detection")
- [Detecting Source Code Plagiarism on Introductory Programming Course Assignments Using a Bytecode Approach - Oscar Karnalim](https://ieeexplore.ieee.org/abstract/document/7910274 "Detecting Source Code Plagiarism on Introductory Programming Course Assignments Using a Bytecode Approach - Oscar Karnalim")
- [Detecting Disguised Plagiarism - Hatem A. Mahmoud](https://arxiv.org/abs/1711.02149 "Detecting Disguised Plagiarism - Hatem A. Mahmoud")
- [Instructor-centric source code plagiarism detection and plagiarism corpus](https://dl.acm.org/doi/abs/10.1145/2325296.2325328 "Instructor-centric source code plagiarism detection and plagiarism corpus")

### Steps Towards Plagiarism
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

### JPlag - End To End TestSuite Structure
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

The values compared so far to detect a match are limited to the similarity of the JPlag scan. It is already planned to expand the comparative values in order to have more indications of a change. 

The Current Discussion is already taking place and can be found at [End to end testing - "comparative values"](https://github.com/jplag/JPlag/issues/548 "End to end testing - \"comparative values\""). 

The current JPlag scans will be compared with the stored ones.
This was done by storing the data in a *.json file which is read at the beginning of each test run. 

``` json  
{
[...]
    "function_name": "normalizationLevelTest",
    "test_results": [
      {
        "result_similarity": 100,
        "test_identifier": 0
      },
      {
        "result_similarity": 100,
        "test_identifier": 1
      },
      {
        "result_similarity": 100,
        "test_identifier": 2
      }
    ]
  }
[...]
```
