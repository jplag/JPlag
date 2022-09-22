<p align="center"> 
	<img alt="JPlag logo" src="core/src/main/resources/de/jplag/logo-dark.png" width="350">
</p>

# JPlag - Detecting Software Plagiarism
[![CI Build](https://github.com/jplag/jplag/actions/workflows/maven.yml/badge.svg)](https://github.com/jplag/jplag/actions/workflows/maven.yml)
[![Latest Release](https://img.shields.io/github/release/jplag/jplag.svg)](https://github.com/jplag/jplag/releases/latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.jplag/jplag/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.jplag/jplag)
[![License](https://img.shields.io/github/license/jplag/jplag.svg)](https://github.com/jplag/jplag/blob/master/LICENSE)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/y/jplag/JPlag)](https://github.com/jplag/JPlag/pulse)

JPlag is a system that finds similarities among multiple sets of source code files. This way it can detect software plagiarism and collusion in software development. JPlag currently supports various programming languages, EMF metamodels, and natural language text.

## Supported Languages

In the following, a list of all supported languages with their supported language version is provided. A language can be selected from the command line using the `-l <cli argument name>` argument.

| Language                                                         | Version | CLI Argument Name     | [state](https://github.com/jplag/JPlag/wiki/3.-Language-Modules) | parser |
|------------------------------------------------------------------|--------:|-----------------------| :---: | :---: |
| [Java](https://www.java.com)                                     |      17 | java                  | mature | JavaC |
| [C++](https://isocpp.org)                                        |      11 | cpp                   | legacy | JavaCC |
| [C#](https://docs.microsoft.com/en-us/dotnet/csharp/)            |       8 | csharp                | beta | ANTLR 4 |
| [Go](https://go.dev)                                             |    1.17 | golang                | beta | ANTLR 4 |
| [Kotlin](https://kotlinlang.org)                                 |     1.3 | kotlin                | beta | ANTLR 4 |
| [Python](https://www.python.org)                                 |     3.6 | python3               | legacy | ANTLR 4 |
| [R](https://www.r-project.org/)                                  |   3.5.0 | rlang                 | beta | ANTLR 4 |
| [Rust](https://www.rust-lang.org/)                               |  1.60.0 | rust                  | beta | ANTLR 4 |
| [Scala](https://www.scala-lang.org)                              |  2.13.8 | scala                 | beta | Scalameta |
| [Scheme](http://www.scheme-reports.org)                          |       ? | scheme                | unknown | JavaCC |
| [EMF Metamodel](https://www.eclipse.org/modeling/emf/)           |  2.25.0 | emf-metamodel         | alpha | EMF |
| [EMF Metamodel](https://www.eclipse.org/modeling/emf/) (dynamic) |  2.25.0 | emf-metamodel-dynamic | alpha | EMF |
| Text (naive)                                                     |       - | text                  | legacy | CoreNLP |

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
  -l               {java,python3,cpp,csharp,golang,kotlin,rlang,rust,scala,text,scheme,emf-metamodel,emf-metamodel-dynamic} Select the language to parse the submissions (default: java)
  -bc BC           Path of the directory containing the base code (common framework used in all submissions)
  -v               {quiet,long} Verbosity of the logging (default: quiet)
  -d               Debug parser. Non-parsable files will be stored (default: false)
  -S S             Look in directories <root-dir>/*/<dir> for programs
  -p P             comma-separated list of all filename suffixes that are included
  -x X             All files named in this file will be ignored in the comparison (line-separated list)
  -t T             Tunes the comparison sensitivity by adjusting the minimum token  required  to be counted as a matching section. A smaller
                        <n> increases the sensitivity but might lead to more false-positives
  -m M             Comparison similarity threshold [0.0-1.0]: All comparisons above this threshold will be saved (default: 0.0)
  -n N             The maximum number of comparisons that will be shown in the  generated report, if set to -1 all comparisons will be shown
                        (default: 30)
  -r R             Name of the directory in which the comparison results will be stored (default: result)
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
ReportObjectFactory reportObjectFactory = new ReportObjectFactory();
reportObjectFactory.createAndSaveReport(result, "/path/to/output");

report.writeResult(result);
```

## Contributing
We're happy to incorporate all improvements to JPlag into this codebase. Feel free to fork the project and send pull requests.
Please consider our [guidelines for contributions](https://github.com/jplag/JPlag/wiki/3.-Contributing-to-JPlag).

## Contact
If you encounter bugs or other issues, please report them [here](https://github.com/jplag/jplag/issues).
For other purposes, you can contact us at jplag@ipd.kit.edu .
If you are doing research related to JPlag, we would love to know what you are doing. Feel free to contact us!

### More information can be found in our [Wiki](https://github.com/jplag/JPlag/wiki)!
