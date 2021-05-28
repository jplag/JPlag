# JPlag - Detecting Software Plagiarism

[![Maven Workflow](https://github.com/jplag/jplag/actions/workflows/maven.yml/badge.svg)](https://github.com/jplag/jplag/actions/workflows/maven.yml)
[![Latest Release](https://img.shields.io/github/release/jplag/jplag.svg)](https://github.com/jplag/jplag/releases/latest)
[![License](https://img.shields.io/github/license/jplag/jplag.svg)](https://github.com/jplag/jplag/blob/master/LICENSE)

## Download and Installation

### Building from sources 

1. Download or clone the code from this repository.
2. Run `mvn clean install` from the root of the repository to install all submodules. You will find the JARs in the respective `target` directories.
3. Inside the `jplag` directory run `mvn clean generate-sources package assembly:single`. 

You'll find the generated JAR with all dependencies in  `jplag/target`.

## (Breaking) Changes coming with the v3.0.0 release

> Note: The following list is incomplete and gives a rough overview of the changes. In case you depend on missing features we refer to the [legacy release v2.12.1](https://github.com/jplag/jplag/releases/tag/v2.12.1-SNAPSHOT) and the [legacy branch](https://github.com/jplag/jplag/tree/legacy).

* Removed folders related to (deprecated) web services: `adminTool`, `atujplag`, `homepage`, `maven-libs`, `webService`, and `wsClient`
* Deleted unnecessary resources from `jplag/src/main/resources`
* All Cluster-related code fragments are commented and marked as `TODO`
* The new `JPlagOptions` class replaces all previous Options-related classes and manages all available program options
* Added the `argparse4j` package to properly parse CLI arguments
* Renamed the `Program` class to `JPlag`. It contains the main logic the parse submissions and delegate the comparison of files to one of the new `ComparisonStrategy` implementing classes.
* Deleted the **experimental** comparison mode. The **external** and **special** comparsion strategies are commented and marked as `TODO`. The **Normal** and **Revision** strategy should work as intended.
* The new `JPlagResult` class is supposed to bundle all results of a plagiarism detection run. An instance of this class can optionally be passed to the new `Report` class to generate web pages of the results
* While re-implementing the CLI, we renamed/removed some arguments to adapt the CLI to the new code structure. A detailed description of all available options of the new CLI can be found below.
* The new `JPlagComparison` class replaces the old `AllMatches` class.
* We removed `AvgComparator`, `AvgReversedComparator`, `MaxComparator`, `MaxReversedComparator`, `MinComparator`, and `MinReversedComparator` from the `JPlagComparison` (previously `AllMatches`) class. All comparisons are now sorted by average similarity. The `JPlagResult` and `JPlagComparison` classes should make adding a custom sorting logic very straightforward if that's required.

## Usage

### CLI

```
usage: jplag [-h]
             [-l {java_1_1,java_1_2,java_1_5,java_1_5_dm,java_1_7,java_1_9,python_3,c_cpp,c_sharp,char,text,scheme}]
             [-bc BC] [-v {parser,quiet,long,details}] [-d] [-S S] [-p P]
             [-x X] [-t T] [-s S] [-r R] rootDir

JPlag - Detecting Software Plagiarism

positional arguments:
  rootDir                The root-directory that contains all submissions

named arguments:
  -h, --help             show this help message and exit
  -l {java_1_1,java_1_2,java_1_5,java_1_5_dm,java_1_7,java_1_9,python_3,c_cpp,c_sharp,char,text,scheme}
                         Select  the  language  to  parse  the  submissions
                         (default: java_1_9)
  -bc BC                 Name of  the  directory  which  contains  the base
                         code (common framework)
  -v {parser,quiet,long,details}
                         Verbosity (default: quiet)
  -d                     (Debug) parser. Non-parsable files  will be stored
                         (default: false)
  -S S                   Look   in   directories   <root-dir>/*/<dir>   for
                         programs
  -p P                   comma-separated  list  of  all  filename  suffixes
                         that are included
  -x X                   All files named in <file> will be ignored
  -t T                   Tune the sensitivity of  the comparison. A smaller
                         <n> increases the sensitivity
  -s S                   Similarity  Threshold:  all   matches  above  this
                         threshold will be saved
  -r R                   Name of directory in which  the  web pages will be
                         stored (default: result)
```

### Java API

The new API makes it easy to integrate JPlag's plagiarism detection into external Java projects.

#### Example 

```java
JPlagOptions options = new JPlagOptions("/path/to/rootDir", LanguageOption.JAVA_1_9);
options.setBaseCodeSubmissionName("template");

JPlag jplag = new JPlag(options);
JPlagResult result = jplag.run();

List<JPlagComparison> comparisons = result.getComparisons();

// Optional
File outputDir = new File("/path/to/output");
Report report = new Report(outputDir);

report.writeResult(result);
```

#### Class Diagram
<p align="center">
	<img alt="UMLClassDiagram.png" src="UMLClassDiagram.png?raw=true" width="800">
</p>

## Concepts

This section explains some fundamental concepts about JPlag that make it easier to understand and use.

### Root directory

This is the directory in which JPlag will scan for submissions.

### Submissions

Submissions contain the source code that JPlag will parse and compare. They have to be direct children of the root directory and can either be single files or directories.

#### Example: Single-file submissions

```
/path/to/root-directory
├── Submission-1.java
├── ...
└── Submission-n.java
```

#### Example: Directory submissions

JPlag will read submission directories recursively, so they can contain multiple (nested) source code files.

```
/path/to/root-directory
├── Submission-1
│   ├── Main.java
│   └── util
│       └── Utils.java
├── ...
└── Submission-n
    ├── Main.java
    └── util
        └── Utils.java
```

If you want JPlag to scan only one specific subdirectory of a submission for source code files (e.g. `src`), you can pass the `--subDir` option:

```
With option --subDir=src

/path/to/root-directory
├── Submission-1
│   ├── src                 
│   │   ├── Main.java       # Included
│   │   └── util            
│   │       └── Utils.java  # Included
│   ├── lib                 
│   │   └── Library.java    # Ignored
│   └── Other.java          # Ignored
└── ...
```

### Base Code

The base code is a special kind of submission. It is the template that all other submissions are based on. JPlag will ignore any match between two submissions that is also part of the base code.

Like any other submission, the base code has to be a single file or directory in the root directory.

```
/path/to/root-directory
├── BaseCode
│   └── Solution.java
├── Submission-1
│   └── Solution.java
├── ...
└── Submission-n
    └── Solution.java
```

#### Example

In this example, students have to solve a given problem by implementing the `run` method in the template below. Because they are not supposed to modify the `main` function, it will be identical for each student. 

```java
// BaseCode/Solution.java
public class Solution {

    // DO NOT MODIFY
    public static void main(String[] args) {
        Solution solution = new Solution();  
        solution.run();
    }
    
    public void run() {
        // TODO: Implement your solution here.
    }
}
```

To prevent JPlag from detecting similarities in the `main` function (and other parts of the template), we can instruct JPlag to ignore matches with the given base code by providing the `--baseCode=<base-code-name>` option. 

The `<base-code-name>` in the example above is `BaseCode`.

## Contributing
We're happy to incorporate all improvements to JPlag into this code base. Feel free to fork the project and send pull requests.

### Adding new languages
Adding a new language frontend is quite simple. Have a look at one of the `jplag.frontend` projects. All you need is a parser for the language (e.g., for ANTLR or for JavaCC) and a few lines of code that sends the tokens (that are generated by the parser) to JPlag.
