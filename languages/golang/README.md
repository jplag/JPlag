# JPlag Go language frontend

The JPlag Go frontend allows the use of JPlag with submissions in Go. <br>
It is based on the [Golang ANTLR4 grammar](https://github.com/antlr/grammars-v4/tree/master/golang), licensed under BSD-3.

### Go specification compatibility

The underlying grammar definition does not specify which version of Go it is built on. This is what we know:
 - Number literal prefixes, a feature of go1.13, are included.
 - Generics, a feature of go1.18, are _not_ included.
 - Between go1.13 and go1.18, there were no changes to the syntax. So, the grammar should be fully compatible with go1.17, released in mid-2021.

If the grammar is updated to a more recent<a href="#footnote-1"><sup>1</sup></a> syntax definition, this module should surely be updated as well.

### Token Extraction

The choice of tokens is intended to be similar to the Java or C# frontends. Specifically, among others, it includes a range of nesting structures (class and method declarations, control flow expressions) as well as variable declaration, object creation, assignment, and control flow altering keywords. <br>
Blocks are distinguished by their context, i.e. there are separate `TokenTypes` for `if` blocks, `for` blocks, class bodies, method bodies, array constructors, and the like.

More syntactic elements of Go may turn out to be helpful to include in the future, especially those that are newly introduced.

### Usage

To use the Go frontend, add the `-l golang` flag in the CLI, or use a `JPlagOption` object with `new de.jplag.golang.Language()` as `language` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

<br>

#### Footnotes
<section id="footnote-1"><sup>1 </sup>The grammar files are taken from grammar-v4, with the most recent modifiactions in <a href="https://github.com/antlr/grammars-v4/tree/51ecccf87b75e96177287367b96cfa99e9f304b8/golang">commit 51ecccf</a> from April 2022.</section>