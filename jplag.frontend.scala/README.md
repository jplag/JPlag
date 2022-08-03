# JPlag Scala language frontend

The JPlag Scala frontend allows the use of JPlag with submissions in Scala. <br>
It is based on the [Scalameta library](https://scalameta.org/) parser, and is adapted from the [CodeGra-de Scala frontend](https://github.com/CodeGra-de/jplag/tree/master/jplag.frontend.scala) for JPlag, both licensed under BSD-3.

### Scala specification compatibility

The dependencies only allow compatibility up to Scala 2.12 (April 2018), so more recent syntactical features like enums are not supported yet. 

Due to intercompatibility issues between dependencies, previous attempts to upgrade to Scala 3 were unsuccessful. We will continue our efforts to support Scala 3. 
 
### Token Extraction

The choice of tokens is intended to be similar to the Java or C# frontends. Specifically, among others, it includes a range of nesting structures (class and method declarations, control flow expressions) as well as variable declaration, object creation, assignment, and control flow altering keywords. <br>
Blocks are distinguished by their context, i.e. there are separate `TokenConstants` for `if` blocks, `for` blocks, class bodies, method bodies, array constructors, and the like.

More syntactic elements of Scala may turn out to be helpful to include in the future, especially those that are newly introduced.

### Usage

To use the Scala frontend, add the `-l scala` flag in the CLI, or use a `JPlagOption` object set to `LanguageOption.SCALA` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).
