# JPlag Kotlin language frontend

The JPlag Kotlin frontend allows the use of JPlag with submissions in Kotlin. <br>
It is based on the [Kotlin ANTLR4 grammar](https://github.com/antlr/grammars-v4/tree/master/kotlin/kotlin), licensed under the Apache 2.0.

### Kotlin specification compatibility

The underlying grammar definition does not specify which version of Kotlin it is built on, but the presence of the `inline` keyword and the lack of support for trailing commas in argument lists indicate that it complies with Kotlin 1.3, released in late 2018.

If there are any major updates or fixes to the grammar<a href="#footnote-1"><sup>1</sup></a>, they should surely be applied to this module as well. 


### Token Extraction

The choice of tokens is intended to be similar to the Java or C# frontends. It includes a range of nesting structures (class, method, control flow expressions) as well as variable declaration, object creation, assignment, and control flow altering keywords.

More syntactic elements of Kotlin may turn out to be helpful to include in the future.

### Usage

To use the Kotlin frontend, add the `-l kotlin` flag in the CLI, or use a `JPlagOption` object set to `LanguageOption.KOTLIN` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

<br>

#### Footnotes
<section id="footnote-1"><sup>1 </sup>Currently, the grammar is taken from grammar-v4, commit 9644ff9 from February 2021.</section>