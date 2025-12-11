<p align="center"> 
	<img alt="JPlag logo" src="core/src/main/resources/de/jplag/logo-dark.png" width="350">
</p>

# JPlag - Detecting Source Code Plagiarism
[![CI Build](https://github.com/jplag/jplag/actions/workflows/build-maven.yml/badge.svg)](https://github.com/jplag/jplag/actions/workflows/build-maven.yml)
[![Latest Release](https://img.shields.io/github/release/jplag/jplag.svg)](https://github.com/jplag/jplag/releases/latest)
[![Maven Central](https://img.shields.io/maven-metadata/v.svg?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fde%2Fjplag%2Fjplag%2Fmaven-metadata.xml&label=maven-central&color=00ff00)](https://central.sonatype.com/artifact/de.jplag/jplag)
[![License](https://img.shields.io/github/license/jplag/jplag.svg)](https://github.com/jplag/jplag/blob/main/LICENSE)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/y/jplag/JPlag)](https://github.com/jplag/JPlag/pulse)
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=jplag_JPlag&metric=coverage)](https://sonarcloud.io/component_measures?metric=Coverage&view=list&id=jplag_JPlag)
[![Java Version](https://img.shields.io/badge/java-SE%2025-yellowgreen)](#download-and-installation)


JPlag finds pairwise similarities among a set of multiple programs. It can reliably detect software plagiarism and collusion in software development, even when obfuscated. All similarities are calculated locally; no source code or plagiarism results are ever uploaded online. JPlag supports a large number of languages.

* 📈 [JPlag Demo](https://jplag.github.io/Demo/)

* 📖 [JPlag Wiki](https://github.com/jplag/JPlag/wiki)

* 🏛️ [JPlag on Helmholtz RSD](https://helmholtz.software/software/jplag)

* 🤩 [Give us Feedback in a **short (<5 min) survey**](https://docs.google.com/forms/d/e/1FAIpQLSckqUlXhIlJ-H2jtu2VmGf_mJt4hcnHXaDlwhpUL3XG1I8UYw/viewform?usp=sf_link)


## Supported Languages

All supported languages and their supported versions are listed below.

| Language                                               |                                                                                Version | CLI Argument Name | [state](https://github.com/jplag/JPlag/wiki/2.-Supported-Languages) |  parser   |
|--------------------------------------------------------|---------------------------------------------------------------------------------------:|-------------------|:-------------------------------------------------------------------:|:---------:|
| [Java](https://www.java.com)                           |                                                                                     25 | java              |                               mature                                |   JavaC   |
| [C](https://isocpp.org)                                |                                                                                     11 | c                 |                               legacy                                |  JavaCC   |
| [C++](https://isocpp.org)                              |                                                                                     14 | cpp               |                               mature                                |  ANTLR 4  |
| [C#](https://docs.microsoft.com/en-us/dotnet/csharp/)  |                                                                                      6 | csharp            |                               mature                                |  ANTLR 4  |
| [Python](https://www.python.org)                       |                                                                                    3.6 | python3           |                               mature                                |  ANTLR 4  |
| [JavaScript](https://www.javascript.com/)              |                                                                                    ES6 | javascript        |                                beta                                 |  ANTLR 4  |
| [TypeScript](https://www.typescriptlang.org/)          | [~5](https://github.com/antlr/grammars-v4/tree/master/javascript/typescript/README.md) | typescript        |                                beta                                 |  ANTLR 4  |
| [Go](https://go.dev)                                   |                                                                                   1.17 | golang            |                                beta                                 |  ANTLR 4  |
| [Kotlin](https://kotlinlang.org)                       |                                                                                    1.3 | kotlin            |                               mature                                |  ANTLR 4  |
| [R](https://www.r-project.org/)                        |                                                                                  3.5.0 | rlang             |                               mature                                |  ANTLR 4  |
| [Rust](https://www.rust-lang.org/)                     |                                                                                 1.60.0 | rust              |                               mature                                |  ANTLR 4  |
| [Swift](https://www.swift.org)                         |                                                                                    5.4 | swift             |                                beta                                 |  ANTLR 4  |
| [Scala](https://www.scala-lang.org)                    |                                                                                 2.13.8 | scala             |                               mature                                | Scalameta |
| [LLVM IR](https://llvm.org)                            |                                                                                     15 | llvmir            |                                beta                                 |  ANTLR 4  |
| [Scheme](http://www.scheme-reports.org)                |                                                                                      ? | scheme            |                               legacy                                |  JavaCC   |
| [EMF Metamodel](https://www.eclipse.org/modeling/emf/) |                                                                                 2.25.0 | emf               |                                beta                                 |    EMF    |
| [EMF Model](https://www.eclipse.org/modeling/emf/)     |                                                                                 2.25.0 | emf-model         |                                alpha                                |    EMF    |
| [SCXML](https://www.w3.org/TR/scxml/)                  |                                                                                    1.0 | scxml             |                                alpha                                |    XML    |
| Text (naive, use with caution)                         |                                                                                      - | text              |                               legacy                                |  CoreNLP  |
| Multi-Language                                         |                                                                                      - | multi             |                                alpha                                |     -     |

## Download and Installation
You need Java SE 25 to run or build JPlag.

### Downloading a release
* Download a [released version](https://github.com/jplag/jplag/releases).
* In case you depend on the legacy version of JPlag, we refer to the [legacy release v2.12.1](https://github.com/jplag/jplag/releases/tag/v2.12.1-SNAPSHOT) and the [legacy branch](https://github.com/jplag/jplag/tree/legacy).

### Via Maven
JPlag is released on [Maven Central](https://search.maven.org/search?q=de.jplag), it can be included as follows:
```xml
<dependency>
  <groupId>de.jplag</groupId>
  <artifactId>jplag</artifactId>
  <version><!--desired version--></version>
</dependency>
```

### Building from sources 
1. Download or clone the code from this repository.
2. Run `mvn clean package` from the repository root to compile and build all submodules.
   Run `mvn clean package assembly:single` instead if you need the full jar, which includes all dependencies.
   Run `mvn -P with-report-viewer clean package assembly:single` to build the full jar with the report viewer. In this case, you'll need [Node.js](https://nodejs.org/en/download) installed.
3. You will find the generated JARs (`jplag-x.y.z-jar-with-dependencies.jar`) in the subdirectory `cli/target`.

## Usage
JPlag can either be used via the CLI or directly via its Java API. For more information, see the [usage information in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag). If you are using the CLI, the report viewer UI will launch automatically. No data will leave your computer!

### CLI
*Note that the [legacy CLI](https://github.com/jplag/jplag/blob/legacy/README.md) is varying slightly.*
The language can either be set with the -l parameter or as a subcommand (`jplag [jplag options] -l <language name> [language options]`). A subcommand takes priority over the -l option.
Language-specific arguments can be set when using the subcommand. A list of language-specific options can be obtained by requesting the help page of a subcommand (e.g., `jplag java —h`).

```
Parameter descriptions: 
      [root-dirs[,root-dirs...]...]
                        Root-directory with submissions to check for
                          plagiarism. If mode is set to VIEW, this parameter
                          can be used to specify a report file to open. In that
                          case only a single file may be specified.
      -bc, --bc, --base-code=<baseCode>
                        Path to the base code directory (common framework used
                          in all submissions).
      -l, --language=<language>
                        Select the language of the submissions (default: java).
                          See subcommands below.
      -M, --mode=<{RUN, VIEW, RUN_AND_VIEW, AUTO}>
                        The mode of JPlag. One of: RUN, VIEW, RUN_AND_VIEW,
                          AUTO (default: null). If VIEW is chosen, you can
                          optionally specify a path to an existing report.
      -n, --shown-comparisons=<shownComparisons>
                        The maximum number of comparisons that will be shown in
                          the generated report, if set to -1 all comparisons
                          will be shown (default: 2500)
      -new, --new=<newDirectories>[,<newDirectories>...]
                        Root-directories with submissions to check for
                          plagiarism (same as root).
      --normalize       Activate the normalization of tokens. Supported for
                          languages: Java, C++.
      -old, --old=<oldDirectories>[,<oldDirectories>...]
                        Root-directories with prior submissions to compare
                          against.
      -r, --result-file=<resultFile>
                        Name of the file in which the comparison results will
                          be stored (default: results). Missing .jplag
                          extension will be automatically added.
      -t, --min-tokens=<minTokenMatch>
                        Tunes the comparison sensitivity by adjusting the
                          minimum token required to be counted as a matching
                          section. A smaller value increases the sensitivity
                          but might lead to more false-positives.

Advanced
      --csv-export      Export pairwise similarity values as a CSV file.
      -d, --debug           Store on-parsable files in error folder.
      --encoding=<submissionCharsetOverride>
                        Specifies the charset of the submissions. This disables
                          the automatic charset detection
      --log-level=<{ERROR, WARN, INFO, DEBUG, TRACE}>
                        Set the log level for the cli.
      -m, --similarity-threshold=<similarityThreshold>
                        Comparison similarity threshold [0.0-1.0]: All
                          comparisons above this threshold will be saved
                          (default: 0.0).
      --overwrite       Existing result files will be overwritten.
      -p, --suffixes=<suffixes>[,<suffixes>...]
                        comma-separated list of all filename suffixes that are
                          included.
      -P, --port=<port>     The port used for the internal report viewer (default:
                          1996).
      -s, --subdirectory=<subdirectory>
                        Look in directories <root-dir>/*/<dir> for programs.
      -x, --exclusion-file=<exclusionFileName>
                        All files named in this file will be ignored in the
                          comparison (line-separated list).

Clustering
      --cluster-alg, --cluster-algorithm=<{AGGLOMERATIVE, SPECTRAL}>
                        Specifies the clustering algorithm. Available
                          algorithms: agglomerative, spectral (default:
                          spectral).
      --cluster-metric=<{AVG, MIN, MAX, INTERSECTION, LONGEST_MATCH,
        MAXIMUM_LENGTH}>
                        The similarity metric used for clustering. Available
                          metrics: average similarity, minimum similarity,
                          maximal similarity, matched tokens, number of tokens
                          in the longest match, length of the longer submission
                          (default: average similarity).
      --cluster-skip    Skips the cluster calculation.

Subsequence Match Merging
      --gap-size=<maximumGapSize>
                        Maximal gap between neighboring matches to be merged
                          (between 1 and minTokenMatch, default: 6).
      --match-merging   Enables merging of neighboring matches to counteract
                          obfuscation attempts.
      --neighbor-length=<minimumNeighborLength>
                        Minimal length of neighboring matches to be merged
                          (between 1 and minTokenMatch, default: 2).
      --required-merges=<minimumRequiredMerges>
                        Minimal required merges for the merging to be applied
                          (between 1 and 50, default: 6).

Frequency Analysis
      --analysis-strategy=<{COMPLETE_MATCHES, CONTAINED_MATCHES, SUBMATCHES,
        MATCH_WINDOWS}>
                        Specifies the strategy for frequency analysis, one of:
                          COMPLETE_MATCHES, CONTAINED_MATCHES, SUBMATCHES,
                          MATCH_WINDOWS (default: COMPLETE_MATCHES).
      --frequency       Enables analysis and highlighting of rare matches.
      --weighting=<{PROPORTIONAL, LINEAR, QUADRATIC, SIGMOID}>
                        The function for frequency-based match weighting, one
                          of: PROPORTIONAL, LINEAR, QUADRATIC, SIGMOID
                          (default: SIGMOID).
Languages:
  c
  cpp
  csharp
  emf
  emf-model
  go
  java
  javascript
  kotlin
  llvmir
  multi
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
Language language = new JavaLanguage();
Set<File> submissionDirectories = Set.of(new File("/path/to/rootDir"));
File baseCode = new File("/path/to/baseCode");
JPlagOptions options = new JPlagOptions(language, submissionDirectories, Set.of()).withBaseCodeSubmissionDirectory(baseCode);

try {
    JPlagResult result = JPlag.run(options);

    // Optional
    ReportObjectFactory reportObjectFactory = new ReportObjectFactory(new File("/path/to/output"));
    reportObjectFactory.createAndSaveReport(result);
} catch (ExitException e) {
    // error handling here
} catch (FileNotFoundException e) {
    // handle IO exception here
}
```

## Contributing
We're happy to incorporate all improvements to JPlag into this codebase. Feel free to fork the project and send pull requests.
Please consider our [guidelines for contributions](https://github.com/jplag/JPlag/wiki/3.-Contributing-to-JPlag).

## Contact
If you encounter bugs or other issues, please report them [here](https://github.com/jplag/jplag/issues).
For other purposes, you can contact us at jplag@ipd.kit.edu.
We would love to hear about your research related to JPlag. Feel free to contact us!
