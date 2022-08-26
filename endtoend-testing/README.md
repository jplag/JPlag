# JPlag - End To End Testing
With the help of the end-to-end module, changes to the detection of JPlag are to be tested.
With the help of elaborated plagiarisms, which have been worked out from suggestions in the literature on the topic of "plagiarism detection and avoidance", a wide range of detectable change can be covered. The selected plagiarisms are the decisive factor here as to whether a change in recognition can be perceived. 

## References
These elaborations provide basic ideas on how a modification of the plagiarized source code can look like or be adapted.
These code adaptations refer to a wide range of changes starting from
adding/removing comments to architectural changes in the deliverables.

The following elaborations were used to be able to create the plagiarisms with the largest coverage:
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
The tests are generated dynamically according to the existing test data and allow the creation of endToEnd tests for all supported languages of JPlag without having to make any changes to the code.
The helper loads the existing test data from the designated directory and creates dynamic tests for the individual directories. It is therefore possible to create different test classes for the different languages.
- JPlagTestSuiteHelper:

``` java 
public static Map<LanguageOption, Map<String, Path>> getAllLanguageResources()
```

The list of languages created in this way and their associated data are dynamically generated into test cases in the Test Suite.

``` java
 Collection<DynamicTest> dynamicOverAllTest()
```

In order to be able to distinguish in which domain of the recognition changes have occurred, fine granular test cases are used. These are composed of the changes already mentioned above. The plagiarism is compared with the original delivery and thus it is possible to detect and test small sections of the recognition. 

The comparative values were discussed and tested. The following results of the JPlag scan are used for the comparison:
1. minimal similarity as `float`
2. maximum similarity as `float`
3. matched token numbe as `int`

The comparative values were disscussed and elaborated in the issue [End to end testing - "comparative values"](https://github.com/jplag/JPlag/issues/548 "End to end testing - \"comparative values\""). 

Additionally it is possible to create several options for the test data. More information about the test options can be found at [JPlag - option variants for the endToEnd tests #590](https://github.com/jplag/JPlag/issues/590 "JPlag - option variants for the endToEnd tests #590"). Currently, various settings are supported by the `minimumTokenMatch`.  This can be extended as desired in the record class `Options`.

The current JPlag scans will be compared with the stored ones.
This was done by storing the data in a *.json file which is read at the beginning of each test run.

``` json  
[...]
{
  "options" : {
    "minimum_token_match" : 1
  },
  "tests" : {
    "SortAlgo-SortAlgo5" : {
      "minimal_similarity" : 82.14286,
      "maximum_similarity" : 82.14286,
      "matched_token_number" : 46
    },
    "SortAlgo-SortAlgo6" : {
      "minimal_similarity" : 83.58209,
      "maximum_similarity" : 100.0,
      "matched_token_number" : 56
    },
    "SortAlgo-SortAlgo7" : {
      "minimal_similarity" : 96.42857,
      "maximum_similarity" : 100.0,
      "matched_token_number" : 54
    },
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

- At the path `JPlag\jplag.endToEndTesting\src\test\resources\languageTestFiles` a new folder for the language should be created if it does not already exist. For example `[...]\resources\languageTestFiles\JAVA`. If you have plagiarized several different code samples, you can also create additional subfolders under the newly created folder for example `[...]\resources\languageTestFiles\JAVA\sortAlgo`.

It is important to note that the resource folder name must be exactly the same as the language identifier name in JPlag/Language. Otherwise the language option cannot be parsed correctly to the enum-type.
 - c++ with "cpp"
 - c# with "csharp"
 - GO with "go"
 - Java with "java"
 - Kotlin with "kotlin"
 - Python3 with "python3"
 - R with "rlang"
 - Rust with "rust"
 - Scala with "scala"

Once the tests have been run for the first time, the information for the tests is stored in the folder `..\target\testing-directory-submission\LANGUAGE`.  This data can be copied to the path `[...]\resources\results\LANGUAGE`. Each subdirectory gets its own result json file as `[...]\resources\results\JAVA\sortAlgo.json`. Once the test data has been copied, the endToEnd tests can be successfully tested. As soon as a change in the detection takes place, the results will differ from the stored results and the tests will fail if the results have changed.
