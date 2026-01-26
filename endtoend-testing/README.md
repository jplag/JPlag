# JPlag - End-To-End Testing

The end-to-end test module contains tests that report any chance in the similarities reported by JPlag.
There are two kinds of tests:
1. Simple tests that fail if the similarity between two submissions changed
2. Gold standard tests

## Gold standard tests

A gold standard test serves as a metric for the change in detection quality. It needs a list of plagiarism instances in the data set.
JPlag outputs comparisons split into those that should be reported as plagiarism and those that shouldn't.
The test will fail if the average similarity on one of those groups changed. In contrast to the other kind of test, this offers a rough way to check if the changes made JPlag better or worse.

## Updating tests

If the similarities reported by JPlag change and these changes are wanted, the reference values for the end-to-end tests need to be updated.
To do that the test in [EndToEndGeneratorTest.java](src/test/java/de/jplag/endtoend/EndToEndGeneratorTest.java) have to be executed.
This will generate new reference files.

## Adding new tests

This segment explains the steps for adding new test data

### Obtain test data

New test data can be obtained in multiple ways.

Ideally, real-world data is used. To use gold standard tests, real-world data needs to contain information about which submission pairs are plagiarism and which aren't.

Alternatively, test data can be generated using various methods. One such method is explained below.

The test data should be placed under [data](src/test/resources/data). It can either be added as a directory containing submissions or as a zip file.

### Defining the data set for the tests

This is done in [dataSets](src/test/resources/dataSets). To add a new data set a new json file needs to be placed here.

A minimum example for a configuration can be found in [progpedia.json](src/test/resources/dataSets/progpedia.json). A full example using all options in [sortAlgo.json](src/test/resources/dataSets/sortAlgo.json).

For available options look at [dataSetTemplate.json](src/test/resources/dataSetTemplate.json).

### Generating the reference results

See Updating tests above

## Creating test data manually

The following changes were applied to sample tasks to create the sortAlgo data set:

* Inserting comments or empty lines (normalization level)
* Changing variable names or function names (normalization level)
* Insertion of unnecessary or changed code lines (token generation)
* Changing the program flow (token generation) (statements and functions must be independent of each other)
  * Variable declaration at the beginning of the program
  * Combining declarations of variables
  * Reuse of the same variable for other functions
* Changing control structures
  * for(...) to while(...)
  * if(...) to switch-case
* Modification of expressions
  * (X < Y) to !(X >= Y) and ++x to x = x + 1
* Splitting and merging statements
  * x = getSomeValue(); y = x- z; to y = (getSomeValue() - Z;

More detailed information about the creation as well as about the subject of the issue can be found in the issue [Develop an end-to-end testing strategy](https://github.com/jplag/JPlag/issues/193 "Develop an end-to-end testing strategy").

**The changes listed above have been developed and evaluated for purely scientific purposes and are not intended to be used for plagiarism in the public or private domain.**

### Creating The Plagiarism

Before you add a new language to the end-to-end tests, I would like to point out that the quality of the tests depends dreadfully on the plagiarism techniques you choose, which were explained in section [Steps Towards Plagiarism](#steps-towards-plagiarism).
If you need more information about creating plans for this purpose, you can also read the elaborations that can be found under [References](#references).
The more various changes you apply, the more accurate the end-to-end tests for the language will be.

In the following, an example is shown, which is in the JavaEndToEnd tests and is used.

**Changing control structures for(…) to while(…):**

```JAVA
//base class
public class SortAlgo {
//...
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
//...
}
```

```JAVA
//created plagiarism
public class SortAlgo5{
//...
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
			I--;
		}
	}
//...
}
```

## References
These elaborations provide basic ideas on how a modification of the plagiarized source code can look or be adapted.
These code adaptations refer to various changes, from
adding/removing comments to architectural changes in the deliverables.

The following elaborations were used to be able to create the plagiarisms with the broadest coverage:
- [Detecting Source Code Plagiarism on Introductory Programming Course Assignments Using a Bytecode Approach - Oscar Karnalim](https://ieeexplore.ieee.org/abstract/document/7910274 "Detecting Source Code Plagiarism on Introductory Programming Course Assignments Using a Bytecode Approach - Oscar Karnalim")
- [Detecting Disguised Plagiarism - Hatem A. Mahmoud](https://arxiv.org/abs/1711.02149 "Detecting Disguised Plagiarism - Hatem A. Mahmoud")
- [Instructor-centric source code plagiarism detection and plagiarism corpus](https://dl.acm.org/doi/abs/10.1145/2325296.2325328 "Instructor-centric source code plagiarism detection and plagiarism corpus")
