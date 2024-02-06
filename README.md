<p align="center"> 
	<img alt="JPlag logo" src="core/src/main/resources/de/jplag/logo-dark.png" width="350">
</p>

# JPlag - Detecting Software Plagiarism
[![CI Build](https://github.com/jplag/jplag/actions/workflows/maven.yml/badge.svg)](https://github.com/jplag/jplag/actions/workflows/maven.yml)
[![Latest Release](https://img.shields.io/github/release/jplag/jplag.svg)](https://github.com/jplag/jplag/releases/latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.jplag/jplag/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.jplag/jplag)
[![License](https://img.shields.io/github/license/jplag/jplag.svg)](https://github.com/jplag/jplag/blob/main/LICENSE)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/y/jplag/JPlag)](https://github.com/jplag/JPlag/pulse)
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=jplag_JPlag&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=jplag_JPlag)
[![Report Viewer](https://img.shields.io/badge/report%20viewer-online-b80025)](https://jplag.github.io/JPlag/)
[![Java Version](https://img.shields.io/badge/java-SE%2021-yellowgreen)](#download-and-installation)


JPlag is a system that finds similarities among multiple sets of source code files. This way it can detect software plagiarism and collusion in software development. JPlag currently supports various programming languages, EMF metamodels, and natural language text.

## Supported Languages

In the following, a list of all supported languages with their supported language version is provided. A language can be selected from the command line using subcommands (jplag [jplag options] <language name> [language options]). Alternatively you can use the legacy "-l" argument.

| Language                                               |                                                                                Version | CLI Argument Name | [state](https://github.com/jplag/JPlag/wiki/2.-Supported-Languages) |  parser   |
|--------------------------------------------------------|---------------------------------------------------------------------------------------:|-------------------|:-------------------------------------------------------------------:|:---------:|
| [Java](https://www.java.com)                           |                                                                                     21 | java              |                               mature                                |   JavaC   |
| [C/C++](https://isocpp.org)                            |                                                                                     11 | cpp               |                               legacy                                |  JavaCC   |
| [C/C++](https://isocpp.org)                            |                                                                                     14 | cpp2              |                                beta                                 |  ANTLR 4  |
| [C#](https://docs.microsoft.com/en-us/dotnet/csharp/)  |                                                                                      6 | csharp            |                                beta                                 |  ANTLR 4  |
| [Go](https://go.dev)                                   |                                                                                   1.17 | golang            |                                beta                                 |  ANTLR 4  |
| [Kotlin](https://kotlinlang.org)                       |                                                                                    1.3 | kotlin            |                                beta                                 |  ANTLR 4  |
| [Python](https://www.python.org)                       |                                                                                    3.6 | python3           |                               legacy                                |  ANTLR 4  |
| [R](https://www.r-project.org/)                        |                                                                                  3.5.0 | rlang             |                                beta                                 |  ANTLR 4  |
| [Rust](https://www.rust-lang.org/)                     |                                                                                 1.60.0 | rust              |                                beta                                 |  ANTLR 4  |
| [Scala](https://www.scala-lang.org)                    |                                                                                 2.13.8 | scala             |                                beta                                 | Scalameta |
| [Scheme](http://www.scheme-reports.org)                |                                                                                      ? | scheme            |                               unknown                               |  JavaCC   |
| [Swift](https://www.swift.org)                         |                                                                                    5.4 | swift             |                                beta                                 |  ANTLR 4  |
| [EMF Metamodel](https://www.eclipse.org/modeling/emf/) |                                                                                 2.25.0 | emf               |                                beta                                 |    EMF    |
| [EMF Model](https://www.eclipse.org/modeling/emf/)     |                                                                                 2.25.0 | emf-model         |                                alpha                                |    EMF    |
| [LLVM IR](https://llvm.org)                            |                                                                                     15 | llvmir            |                                beta                                 |  ANTLR 4  |
| [TypeScript](https://www.typescriptlang.org/)          | [~5](https://github.com/antlr/grammars-v4/tree/master/javascript/typescript/README.md) | typescript        |                                beta                                 |  ANTLR 4  |
| JavaScript                                             |                                                                                    ES6 | javascript        |                                beta                                 |  ANTLR 4  |
| Text (naive)                                           |                                                                                      - | text              |                               legacy                                |  CoreNLP  |

## Download and Installation
You need Java SE 21 to run or build JPlag.

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
   Run `mvn -Pwith-report-viewer clean package assembly:single` to build the full jar with the report viewer. In this case, you'll need `npm` installed.
3. You will find the generated JARs in the subdirectory `cli/target`.

## Usage
JPlag can either be used via the CLI or directly via its Java API. For more information, see the [usage information in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag). If you are using the CLI, you can display your results via [jplag.github.io](https://jplag.github.io/JPlag/). No data will leave your computer!

### CLI
*Note that the [legacy CLI](https://github.com/jplag/jplag/blob/legacy/README.md) is varying slightly.*

The language can either be set with the -l parameter or as a subcommand. If both a subcommand and the -l option are specified, the subcommand will take priority.
When using the subcommand language specific arguments can be set.
A list of language specific options can be obtained by requesting the help page of a subcommand (e.g. "jplag java -h").

```
Usage: jplag [OPTIONS] [root-dirs[,root-dirs...]...] [COMMAND]

      [root-dirs[,root-dirs...]...]
                       Root-directory with submissions to check for plagiarism

      -bc, --bc, --base-code=<baseCode>
                       Path of  the  directory  containing  the  base  code
                         (common  framework  used  in  all submissions)

      -h, --help           display this help and exit
      -l, --language=<language>
                       Select the language to parse the submissions (default:
                         java). The language names are the same as the
                         subcommands.

      -n, --shown-comparisons=<shownComparisons>
                       The maximum number of comparisons that will  be  shown
                         in the generated report, if set to -1 all comparisons
                         will be shown (default: 100)

      -new, --new=<newDirectories>[,<newDirectories>...]
                       Root-directory with submissions to check for plagiarism
                         (same as the root directory)

      -old, --old=<oldDirectories>[,<oldDirectories>...]
                       Root-directory with prior submissions to compare against

      -r, --result-directory=<resultFolder>
                       Name of the directory in which the comparison results
                         will be stored (default: result)

      -t, --min-tokens=<minTokenMatch>
                       Tunes the comparison sensitivity by adjusting the
                         minimum token required to be counted as a matching
                         section. A smaller <n>  increases  the sensitivity but
                         might lead to more false-positives

Advanced
      -d, --debug          Debug parser. Non-parsable files will be stored
                         (default: false)

      -m, --similarity-threshold=<similarityThreshold>
                       Comparison similarity threshold [0.0-1.0]:  All
                         comparisons  above this threshold will be saved
                         (default: 0.0)

      -p, --suffixes=<suffixes>[,<suffixes>...]
                       comma-separated list of all filename suffixes that are
                         included

      -s, --subdirectory=<subdirectory>
                       Look in directories <root-dir>/*/<dir> for programs

      -x, --exclusion-file=<exclusionFileName>
                       All files named in this file will be ignored in the
                         comparison (line-separated list)

Clustering
      --cluster-alg, --cluster-algorithm=<algorithm>
                       Which clustering algorithm to use. Agglomerative  merges
                         similar submissions bottom up. Spectral clustering is
                         combined  with  Bayesian  Optimization  to  execute
                         the k-Means clustering  algorithm  multiple   times,
                         hopefully   finding   a   "good"  clustering
                         automatically. (default: spectral)

      --cluster-metric=<metric>
                       The metric used for clustering. AVG  is  intersection
                         over  union, MAX can expose some attempts of
                         obfuscation. (default: MAX)

      --cluster-skip   Skips the clustering (default: false)
Commands:
  cpp
  cpp2
  csharp
  emf
  emf-model
  go
  java
  javascript
  kotlin
  llvmir
  python3
  rlang
  rust
  scala
  scheme
  scxml
  swift
  text
  typescript
```

### Java API

The new API makes it easy to integrate JPlag's plagiarism detection into external Java projects:

<!-- To assure that the code example is always correct, it must be kept in sync
with [`ReadmeCodeExampleTest#testReadmeCodeExample`](core/src/test/java/de/jplag/special/ReadmeCodeExampleTest.java). -->
```java
JavaLanguage language = new JavaLanguage();
language.getOptions(); //Use the object returned by this to set language options(same as language specific arguments above).
Set<File> submissionDirectories = Set.of(new File("/path/to/rootDir"));
File baseCode = new File("/path/to/baseCode");
JPlagOptions options = new JPlagOptions(language, submissionDirectories, Set.of()).withBaseCodeSubmissionDirectory(baseCode);

try {
    JPlagResult result = JPlag.run(options);
     
    // Optional
    ReportObjectFactory reportObjectFactory = new ReportObjectFactory();
    reportObjectFactory.createAndSaveReport(result, "/path/to/output");
} catch (ExitException e) {
    // error handling here
}
```

## Contributing
We're happy to incorporate all improvements to JPlag into this codebase. Feel free to fork the project and send pull requests.
Please consider our [guidelines for contributions](https://github.com/jplag/JPlag/wiki/3.-Contributing-to-JPlag).

## Contact
If you encounter bugs or other issues, please report them [here](https://github.com/jplag/jplag/issues).
For other purposes, you can contact us at jplag@ipd.kit.edu .
If you are doing research related to JPlag, we would love to know what you are doing. Feel free to contact us!

### More information can be found in our [Wiki](https://github.com/jplag/JPlag/wiki)!
