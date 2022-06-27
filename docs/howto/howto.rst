How to use JPlag
================

JPlag can be used via the Command Line Interface (CLI) or programmatically via the Java API.

Using JPlag via the CLI
-----------------------

JPlag can be used via the Command Line Interface by executing the JAR file.

Example: `java -jar jplag.jar path/to/the/submissions`

The following arguments can be used to control JPlag:

.. code-block:: none

    Usage: JPlag [ options ] [<root-dir>]
     <root-dir>        The root-directory that contains all submissions

    named arguments:
      -h, --help             show this help message and exit
      -l                     {java,python3,cpp,csharp,char,text,scheme} Select the language to parse the submissions (Standard: java)
      -bc BC                 Name of the subdirectory of the root directory which contains the base code (common framework used in all submissions)
      -v                     {quiet,long} Verbosity of the logging (Standard: quiet)
      -d                     Debug parser. Non-parsable files will be stored (Standard: false)
      -S S                   Look in directories <root-dir>/*/<dir> for programs
      -p P                   comma-separated list of all filename suffixes that are included
      -x X                   All files named in this file will be ignored in the comparison (line-separated list)
      -t T                   Tunes the comparison sensitivity by adjusting the minimum token required to be counted as a matching section. A smaller <n> increases the sensitivity but might lead to more false-positives
      -m M                   Match similarity threshold [0-100]: All matches above this threshold will be saved (Standard: 0.0)
      -n N                   The maximum number of comparisons that will be shown in the generated report, if set to -1 all comparisons will be shown (Standard: 30)
      -r R                   Name of the directory in which the comparison results will be stored (Standard: result)
      -c                     {normal,parallel} Comparison mode used to compare the programs (Standard: normal)


*Note that the `legacy CLI <https://github.com/jplag/jplag/blob/legacy/README.md>`__ is varying slightly.*

Using JPlag programmatically
----------------------------

The new API makes it easy to integrate JPlag's plagiarism detection into external Java projects.

**Example:**

.. code-block:: java

    JPlagOptions options = new JPlagOptions("/path/to/rootDir", LanguageOption.JAVA);
    options.setBaseCodeSubmissionName("template");

    JPlag jplag = new JPlag(options);
    JPlagResult result = jplag.run();

    List<JPlagComparison> comparisons = result.getComparisons();

    // Optional
    File outputDir = new File("/path/to/output");
    Report report = new Report(outputDir, options);

    report.writeResult(result);


**Class Diagram:**

.. uml::
    :caption: JPlag Class Diagram for Java API

    class JPlagResult {
      getDuration() : long
      getSimilarityDistribution() : int[]

    }
    class SubmissionSet
    class JPlagComparison
    class JPlagOptions
    enum LanguageOption {
      JAVA,
      PYTHON_3,
      ...
      getDisplayName() : String,
      getClassPath() : String
    }
    class Submission {
      getName() : String,
      getFiles() : File[],
      getTokenList() : TokenList
    }
    class Match {
      getStartOfFirst() : int,
      getStartOfSecond() : int,
      getLength() : int
    }

    JPlagResult -right-> "1" JPlagOptions
    JPlagOptions -right-> "1" LanguageOption
    JPlagResult --> "1" SubmissionSet
    JPlagResult --> "0..*" JPlagComparison
    SubmissionSet -down-> "0..*" Submission
    JPlagComparison -down-> "2" Submission
    JPlagComparison -right-> "0..*" Match


Basic Concepts
--------------

This section explains some fundamental concepts about JPlag that make it easier to understand and use.

* **Root directory:** This is the directory in which JPlag will scan for submissions.
* **Submissions:** Submissions contain the source code that JPlag will parse and compare. They have to be direct children of the root directory and can either be single files or directories.


**Example:** Single-file submissions

.. code-block:: none

    /path/to/root-directory
    ├── Submission-1.java
    ├── ...
    └── Submission-n.java


**Example:** Directory submissions

JPlag will read submission directories recursively, so they can contain multiple (nested) source code files.

.. code-block:: none

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

If you want JPlag to scan only one specific subdirectory of the submissions for source code files (e.g. `src`), can configure that with the argument `-S`:

.. code-block:: none

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



**Example:** Base Code

The base code is a special kind of submission. It is the template that all other submissions are based on. JPlag will ignore all matches between two submissions, where the matches are also part of the base code. Like any other submission, the base code has to be a single file or directory in the root directory.

.. code-block:: none

    /path/to/root-directory
    ├── BaseCode
    │   └── Solution.java
    ├── Submission-1
    │   └── Solution.java
    ├── ...
    └── Submission-n
        └── Solution.java


In this example, students have to solve a given problem by implementing the `run` method in the template below. Because they are not supposed to modify the `main` function, it will be identical for each student.

.. code-block:: java

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


To prevent JPlag from detecting similarities in the `main` function (and other parts of the template), we can instruct JPlag to ignore matches with the given base code by providing the `--bc=<base-code-name>` option.
The `<base-code-name>` in the example above is `BaseCode`.


**Example:** Parallel Comparison

For large datasets, the parallel comparison strategy allows faster runtime by utilizing all available CPU cores.
Specify this strategy with the argument `-c parallel`. Note that this is not meant to be used whenever you want to use the computer during the comparison for other tasks.
