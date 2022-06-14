# JPlag R language frontend

The JPlag R frontend allows the use of JPlag with submissions in R. 

### R specification compatibility
The underlying [grammar definition](https://github.com/antlr/grammars-v4/tree/master/r) was first created in June 2013, when R 3.0.1 was current. The latest commit is from April 2018, when R 3.5.0 was just released. Whether the grammar has been made to comply with any specific version of the R specification is unclear. Even if some parsing errors occur, the parser should be able to recover and still produce a valid analysis.

### Usage
To use the R frontend, add the `-l R` flag in the CLI, or use a `JPlagOption` object set to `LanguageOption.R` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).