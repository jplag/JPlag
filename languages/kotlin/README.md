# JPlag Kotlin language module

The JPlag Kotlin module allows the use of JPlag with submissions in Kotlin. <br>
It is based on the [Kotlin ANTLR4 grammar](https://github.com/antlr/grammars-v4/tree/master/kotlin/kotlin), licensed under the Apache 2.0.

### Kotlin specification compatibility

The underlying grammar definition does not specify which version of Kotlin it is built on, but based on the supported features it should support everything up to 1.4.

The grammar also supports all features up to 2.0 except for `value class` and the `..<` operator.

### Token Extraction

The choice of tokens is intended to be similar to the Java or C# modules. It includes a range of nesting structures (class, method, control flow expressions) as well as variable declaration, object creation, assignment, and control flow altering keywords.

More syntactic elements of Kotlin may turn out to be helpful to include in the future.

### Usage

To use the Kotlin module, add the `-l kotlin` flag in the CLI, or use a `JPlagOption` object with `new de.jplag.kotlin.KotlinLanguage()` as `language` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).
