<p align="center"> 
	<img alt="JPlag logo" src="https://user-images.githubusercontent.com/4396919/190650441-5c9407a0-94db-4b05-ae0d-518427db3529.png" width=300px>
</p>

## What is JPlag
JPlag finds pairwise similarities among a set of multiple programs. It can reliably detect software plagiarism and collusion in software development. All similarities are calculated locally; no source code or plagiarism results are ever uploaded online. JPlag supports a large number of programming and modeling languages. JPlag does not merely compare bytes of text but is aware of programming language syntax and program structure, and hence is robust against many kinds of attempts to disguise similarities (_obfusction_) between plagiarized files.

JPlag is typically used to detect and thus discourage the unallowed copying of student exercise programs in programming education. However, in principle, it can also detect stolen software parts among large amounts of source text or modules that have been duplicated (and only slightly modified). JPlag has already played a part in several intellectual property cases where expert witnesses have successfully used it.

**Just to make it clear**: JPlag does not compare to the internet! It is designed to find similarities among the student solutions, which is usually sufficient for computer programs.

* 📈 [JPlag Demo](https://jplag.github.io/Demo/)

* 🏛️ [JPlag on Helmholtz RSD](https://helmholtz.software/software/jplag)

* 📚 [Recent Scientific Publications](https://www.jplag.de)

## History
Originally, JPlag was developed in 1996 by Guido Mahlpohl and others at the chair of Prof. Walter Tichy at Karlsruhe Institute of Technology (KIT). It was first documented in a [Tech Report](https://publikationen.bibliothek.kit.edu/542000) in 2000 and later more formally in the [Journal of Universal Computer Science](http://www.ipd.kit.edu/tichy/uploads/publikationen/16/finding_plagiarisms_among_a_set_of_progr_638847.pdf). Since 2015, JPlag has been hosted here on GitHub. Starting in late 2020, JPlag was revived and modernized by Timur Saglam and Sebastian Hahner.
Since then, JPlag has benefited from ongoing contributions by numerous developers.
After 30 years since its creation, JPlag is used frequently in hundreds of universities worldwide.

## Download JPlag
Download the latest version of JPlag [here](https://github.com/jplag/jplag/releases). If you encounter bugs or other issues, please report them [here](https://github.com/jplag/jplag/issues).

## Using JPlag
Use JPlag via the CLI to analyze your set of source codes. You can display your results via [jplag.github.io](https://jplag.github.io/JPlag/). No data will leave your computer! More usage information can be found [here](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

## Include JPlag as a Dependency
JPlag is released on [Maven Central](https://search.maven.org/search?q=de.jplag), it can be included as follows:
```xml
<dependency>
  <groupId>de.jplag</groupId>
  <artifactId>jplag</artifactId>
  <version><!--desired version--></version>
</dependency>
```

## JPlag legacy version
In case you depend on the legacy version of JPlag, we refer to the [legacy release v2.12.1](https://github.com/jplag/jplag/releases/tag/v2.12.1-SNAPSHOT) and the [legacy branch](https://github.com/jplag/jplag/tree/legacy). Note that the legacy CLI and report UI are different and provide fewer features.

## Frequently Asked Questions
The following questions arise frequently. If you have other questions, ask us in the [Q&A discussion section](https://github.com/jplag/JPlag/discussions/categories/q-a).

### 1. How do I exclude template or starter code from plagiarism detection?
Provide the base code using the `--bc` flag. JPlag will ignore matches that are also present in the base code directory.

### 2. How can I adjust the sensitivity of plagiarism detection?
Use the `-t` flag to set the minimum match token length (in tokens). Lower values increase sensitivity but may lead to more false positives. Higher values decrease sensitivity, especially when all submission pairs exhibit high similarity values. The default values depend on the language and are shown in the overview of a report.

### 3. How can I compare submissions from different years or cohorts?
Use the `-new` and `-old` flags to designate root directories containing current and previous submissions. JPlag compares new submissions with each other and with old submissions, but it does not compare old submissions within the old set with each other.

### 4. How do I include or exclude specific files?
Use `-p` to define custom file suffixes required for files to be included, use `-x` to provide a file containing paths to exclude, and `-s` to only target a specific subdirectory inside each submission.

### 5. How do I view existing JPlag reports?
Run JPlag in the view-only mode via the `--mode view` option. 

### 6. Are all program comparisons included in a report?
Only indirectly in the distribution histogram. In the top list and for the code comparison, only the top 2500 pairs are included. This number can be controlled via `-n`.

### 7. How can I improve obfuscation or tampering resilience?
Use the `--match-merging` option to enable the heuristic merging of neighboring matches, which counteracts obfuscation attempts. For Java and C++, use `--normalize` to ignore dead code and normalize the statement order in programs. You can also use both options at the same time.

### 8. How do I archive report files?
You can store the report files. While we strive to achieve backwards compatibility, you can always open them with the version of JPlag they were created with.

### 9. Will sensitive data be uploaded somewhere when using JPlag?
No, JPlag runs entirely locally. Whether you use the command-line tool or integrate the JPlag library into your system, all analysis is performed on your machine, and no data is sent externally.

### 10. Is it possible to run JPlag on large datasets efficiently?
Yes, it is, but performance may vary depending on your system and how much memory Java is provided with.
For example, JPlag can handle ~3600 large submissions (in total, 5 million LoC and 100K files), leading to 6.5 million program comparisons in 15 minutes on an M1 MacBook. If you run into performance issues, consider increasing Java memory (e.g., `java -Xmx16G`) or skipping clustering (`--cluster-skip`).

### 11. Who is behind JPlag?
JPlag is an open-source project primarily developed and maintained by researchers at the Karlsruhe Institute of Technology (KIT) in Germany.
