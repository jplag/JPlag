# JPlag R language module

The JPlag R module allows the use of JPlag with submissions in R. <br>
It was in part adapted from a [JPLag fork by CodeGra-de](https://github.com/CodeGra-de/jplag/tree/master/jplag.module.R).

### R specification compatibility
The underlying [grammar definition](https://github.com/antlr/grammars-v4/tree/master/r) was first created in June 2013, when R 3.0.1 was current. The latest commit is from April 2018, when R 3.5.0 was just released. Whether the grammar has been made to comply with any specific version of the R specification is unclear. Even if some parsing errors occur, the parser should be able to recover and still produce a valid analysis.

### Token Extraction

The choice of tokens is based directly on the CodeGra-de version, whereas the extraction process itself contains some fixes.

Like in other modules, e.g. for Java and C#, the tokens account for the beginning and the end of control flow structures, for control flow keywords, and some kinds of expressions. As R is very different from other programming languages in JPlag, it remains to be seen whether the R module can hold up to the others.

### Usage
To use the R module, add the `-l R` flag in the CLI, or use a `JPlagOption` object with `new de.jplag.rlang.Language()` as `language` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).