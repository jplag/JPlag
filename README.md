<p align="center"> 
	<img alt="JPlag logo" src="jplag/src/main/resources/de/jplag/logo-dark.png" width="350">
</p>

# JPlag - Detecting Software Plagiarism
[![CI Build](https://github.com/jplag/jplag/actions/workflows/maven.yml/badge.svg)](https://github.com/jplag/jplag/actions/workflows/maven.yml)
[![Latest Release](https://img.shields.io/github/release/jplag/jplag.svg)](https://github.com/jplag/jplag/releases/latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.jplag/jplag/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.jplag/jplag)
[![License](https://img.shields.io/github/license/jplag/jplag.svg)](https://github.com/jplag/jplag/blob/master/LICENSE)
[![Lines of code](https://img.shields.io/tokei/lines/github/jplag/jplag)](https://github.com/jplag/jplag/graphs/contributors)

JPlag is a system that finds similarities among multiple sets of source code files. This way it can detect software plagiarism and collusion in software development. JPlag currently supports Java, C#, C, C++, Python 3, Scheme, and natural language text.

## Download and Installation

### Downloading a release
* Download a [released version](https://github.com/jplag/jplag/releases).
* In case you depend on the legacy version of JPlag we refer to the [legacy release v2.12.1](https://github.com/jplag/jplag/releases/tag/v2.12.1-SNAPSHOT) and the [legacy branch](https://github.com/jplag/jplag/tree/legacy).

### Via Maven
JPlag is released on [Maven Central](https://search.maven.org/search?q=de.jplag), it can be included as follows:
```xml
<dependency>
  <groupId>de.jplag</groupId>
  <artifactId>jplag</artifactId>
</dependency>
```

### Building from sources 
1. Download or clone the code from this repository.
2. Run `mvn clean package` from the root of the repository to compile and build all submodules.
   Run `mvn clean package assembly:single` instead if you need the full jar which includes all dependencies.
5. You will find the generated JARs in the subdirectory `jplag.cli/target`.

## Usage
JPlag can either be used via the CLI or directly via its Java API. For more information, see the [usage information in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

### CLI
*Note that the [legacy CLI](https://github.com/jplag/jplag/blob/legacy/README.md) is varying slightly.*

```
JPlag - Detecting Software Plagiarism

Usage: JPlag [ options ] [ <root-dir> ... ] [ -new <new-dir> ... ] [ -old <old-dir> ... ]
 <root-dir>        Root-directory with submissions to check for plagiarism
 <new-dir>         Root-directory with submissions to check for plagiarism
 <old-dir>         Root-directory with prior submissions to compare against

named arguments:
  -h, --help       show this help message and exit
  -l               {java,python3,cpp,csharp,golang,kotlin,rlang,char,text,scheme} Select the language to parse the submissions (default: java)
  -bc BC           Path of the directory containing the base code (common framework used in all submissions)
  -v               {quiet,long} Verbosity of the logging (default: quiet)
  -d               Debug parser. Non-parsable files will be stored (default: false)
  -S S             Look in directories <root-dir>/*/<dir> for programs
  -p P             comma-separated list of all filename suffixes that are included
  -x X             All files named in this file will be ignored in the comparison (line-separated list)
  -t T             Tunes the comparison sensitivity by adjusting the minimum token  required  to be counted as a matching section. A smaller
                        <n> increases the sensitivity but might lead to more false-positives
  -m M             Comparison similarity threshold [0-100]: All comparisons above this threshold will be saved (default: 0.0)
  -n N             The maximum number of comparisons that will be shown in the  generated report, if set to -1 all comparisons will be shown
                        (default: 30)
  -r R             Name of the directory in which the comparison results will be stored (default: result)
  -c               {normal,parallel} Comparison mode used to compare the programs (default: normal)
```

### Java API

The new API makes it easy to integrate JPlag's plagiarism detection into external Java projects:

```java
JPlagOptions options = new JPlagOptions(List.of("/path/to/rootDir"), List.of(), LanguageOption.JAVA);
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
For other purposes, you can contact us at jplag@ipd.kit.edu .
If you are doing research related to JPlag, we would love to know what you are doing. Feel free to contact us!

### More information can be found in our [Wiki](https://github.com/jplag/JPlag/wiki)!
