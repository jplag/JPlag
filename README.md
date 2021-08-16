# JPlag - Detecting Software Plagiarism

[![Maven Workflow](https://github.com/jplag/jplag/actions/workflows/maven.yml/badge.svg)](https://github.com/jplag/jplag/actions/workflows/maven.yml)
[![Latest Release](https://img.shields.io/github/release/jplag/jplag.svg)](https://github.com/jplag/jplag/releases/latest)
[![License](https://img.shields.io/github/license/jplag/jplag.svg)](https://github.com/jplag/jplag/blob/master/LICENSE)

## Download and Installation

### Downloading a release
Download a [released version](https://github.com/jplag/jplag/releases), the most recent is [v2.12.1](https://github.com/jplag/jplag/releases/tag/v2.12.1-SNAPSHOT).
All releases are single-JAR releases.

### Building from sources 
*Note that v3.0.0 is still very much a work in progress, we thus recommend using a released version for now.*
1. Download or clone the code from this repository.
2. Run `mvn clean install` from the root of the repository to install all submodules. You will find the JARs in the respective `target` directories.
3. Inside the `jplag` directory run `mvn clean generate-sources package assembly:single`. 

You'll find the generated JAR with all dependencies in  `jplag/target`.

## (Breaking) Changes coming with the v3.0.0 release

> Note: The following list is incomplete and gives a rough overview of the changes. In case you depend on missing features we refer to the [legacy release v2.12.1](https://github.com/jplag/jplag/releases/tag/v2.12.1-SNAPSHOT) and the [legacy branch](https://github.com/jplag/jplag/tree/legacy).

* Java API to allow 3rd party integration (see Java API usage below).
* Result clustering is currently not supported (but might be restored).
* All comparisons are now sorted by average similarity. Maximum similarity is currently not supported (but might be restored).
* The CLI interface is now properly defined based on `argparse4j`. The CLI interface was slightly changed, a detailed description of all available options of the new CLI can be found below.
* The following comparison modes are no longer supported: **experimental**, **external**, **special** and **revision**.
* Renamed the `Program` class to `JPlag`. It contains the main logic, the parsed submissions, and delegates the comparison of files to one of the new `ComparisonStrategy` implementing classes.
* The new `JPlagOptions` class replaces all previous Options-related classes and manages all available program options.
* The new `JPlagResult` class bundles all results of a plagiarism detection run. An instance of this class can optionally be passed to the new `Report` class to generate web pages for the results
* The new `JPlagComparison` class replaces the old `AllMatches` class.
* We removed `AvgComparator`, `AvgReversedComparator`, `MaxComparator`, `MaxReversedComparator`, `MinComparator`, and `MinReversedComparator` from the `JPlagComparison` (previously `AllMatches`) class. The `JPlagResult` and `JPlagComparison` classes should make adding a custom sorting logic very straightforward if that's required.
* Removed deprecated web service: `adminTool`, `atujplag`, `homepage`, `maven-libs`, `webService`, and `wsClient`

## Usage
JPlag can either be used via the CLI or directly via its Java API.

### CLI
*Note that the [legacy CLI](https://github.com/jplag/jplag/blob/legacy/README.md) is varying slightly.*

```
JPlag - Detecting Software Plagiarism

Usage: JPlag [ options ] [<root-dir>]
 <root-dir>        The root-directory that contains all submissions

named arguments:
  -h, --help             show this help message and exit
  -l                     {java1,java2,java5,java5dm,java7,java9,python3,cpp,csharp,char,text,scheme} Select the language to parse the submissions (Standard: java9)
  -bc BC                 Name of the directory which contains the base code (common framework used in all submissions)
  -v                     {quiet,long} Verbosity of the logging (Standard: quiet)
  -d                     (Debug) parser. Non-parsable files will be stored (Standard: false)
  -S S                   Look in directories <root-dir>/*/<dir> for programs
  -p P                   comma-separated list of all filename suffixes that are included
  -x X                   All files named in this file will be ignored in the comparison (line-separated list)
  -t T                   Tune the sensitivity of the comparison. A smaller <n> increases the sensitivity
  -m M                   Match similarity Threshold [0-100]: All matches above this threshold will be saved (Standard: 0.0)
  -n N                   Maximum number of matches that will be saved. If set to -1 all matches will be saved (Standard: 30)
  -r R                   Name of the directory in which the comparison results will be stored (Standard: result)
  -c {normal,parallel}   Comparison mode used to compare the programs (Standard: normal)
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

If you want JPlag to scan only one specific subdirectory of a submission for source code files (e.g. `src`), you can pass the `-S` option:

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
We're happy to incorporate all improvements to JPlag into this codebase. Feel free to fork the project and send pull requests.

### Adding new languages
Adding a new language frontend is quite simple. Have a look at one of the `jplag.frontend` projects. All you need is a parser for the language (e.g., for ANTLR or for JavaCC) and a few lines of code that send the tokens (that are generated by the parser) to JPlag.
