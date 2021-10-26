<p align="center"> 
	<img alt="JPlag logo" src="jplag/src/main/resources/de/jplag/reporting/data/logo-dark.png">
</p>

# JPlag - Detecting Software Plagiarism

[![Maven Workflow](https://github.com/jplag/jplag/actions/workflows/maven.yml/badge.svg)](https://github.com/jplag/jplag/actions/workflows/maven.yml)
[![Latest Release](https://img.shields.io/github/release/jplag/jplag.svg)](https://github.com/jplag/jplag/releases/latest)
[![License](https://img.shields.io/github/license/jplag/jplag.svg)](https://github.com/jplag/jplag/blob/master/LICENSE)

## Download and Installation

### Downloading a release
* Download a [released version](https://github.com/jplag/jplag/releases), the most recent is [v3.0.0](https://github.com/jplag/jplag/releases/tag/v3.0.0-SNAPSHOT).
* In case you depend on the legacy version of JPlag we refer to the [legacy release v2.12.1](https://github.com/jplag/jplag/releases/tag/v2.12.1-SNAPSHOT) and the [legacy branch](https://github.com/jplag/jplag/tree/legacy).

### Building from sources 
1. Download or clone the code from this repository.
2. Run `mvn clean install` from the root of the repository to install all submodules. You will find the JARs in the respective `target` directories.
3. Inside the `jplag` directory run `mvn clean generate-sources package assembly:single`. 

You will find the generated JAR with all dependencies in  `jplag/target`.

## Usage
JPlag can either be used via the CLI or directly via its Java API. For more information see the [usage information in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

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

The new API makes it easy to integrate JPlag's plagiarism detection into external Java projects:

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

## Contributing
We're happy to incorporate all improvements to JPlag into this codebase. Feel free to fork the project and send pull requests.
Please consider our [guidelines for contributions](https://github.com/jplag/JPlag/wiki/2.-Contributing-to-JPlag).

## Contact
If you encounter bugs or other issues, please report them [here](https://github.com/jplag/jplag/issues).
For other purposes you can contact us at jplag@ipd.kit.edu .
If you are doing research related to JPlag, we would love to know what you are doing. Feel free to contact us!

### More information can be found in our [Wiki](https://github.com/jplag/JPlag/wiki)!
