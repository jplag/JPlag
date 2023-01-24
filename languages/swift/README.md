# JPlag Swift language frontend

The JPlag Swift frontend allows the use of JPlag with submissions in Swift. <br>
It is based on the [Swift ANTLR4 grammar](https://github.com/antlr/grammars-v4/tree/master/swift/swift5), licensed under the Apache 2.0.

### Swift specification compatibility

The underlying grammar definition is based on Swift 5.4.

If there are any major updates or fixes to the grammar<a href="#footnote-1"><sup>1</sup></a>, they should surely be applied to this module as well. 


### Token Extraction

The choice of tokens is intended to be similar to the Java or C# frontends. It includes a range of nesting structures (class, method, control flow expressions) as well as variable declaration, object creation, assignment, and control flow altering keywords.

### Usage

To use the Swift frontend, add the `-l swift` flag in the CLI, or use a `JPlagOption` object with `new de.jplag.swift.Language()` as `language` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

<br>

#### Footnotes
<section id="footnote-1"><sup>1 </sup>The grammar files are taken from grammar-v4, with the most recent modification in <a href="https://github.com/antlr/grammars-v4/tree/d9af329091f9888ffde70aa5cfe84f583b98751a/swift/swift5">commit d9af329</a> from Sep 2022.
They received some minor modifications to support identifying the end of do-blocks.</section>