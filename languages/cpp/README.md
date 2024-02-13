# JPlag C++ language module

**Note**: This replaces the old cpp module, which is now only meant for c, as it works better for c than this one.

The JPlag C++ frontend allows the use of JPlag with submissions in C++. <br>
It is based on the [C++ ANTLR4 grammar](https://github.com/antlr/grammars-v4/tree/master/cpp), licensed under MIT.

### C++ specification compatibility

The grammar definition targets C++14.

If the grammar is updated to a more recent<a href="#footnote-1"><sup>1</sup></a> syntax definition, this module should surely be updated as well.

### Token Extraction

The choice of tokens is intended to be similar to the Java language module.
While the Java language module is based on an AST, this language module uses a parse tree only.
There are differences, including:
- `import` is extracted in Java, while `using` is not extracted due to the fact that it can be placed freely in the code.

More syntactic elements of C++ may turn out to be helpful to include in the future, especially those that are newly introduced.

### Usage

To use the C++ frontend, add the `-l cpp` flag in the CLI, or use a `JPlagOption` object with `new de.jplag.cpp.CPPLanguage()` as `Language` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

### Changes to the Grammar

There is one change in our grammar compared to the original one. In the rule for "theOperator" 'DivAssign' was added as a case.
This is an error in the original version.

<br>

#### Footnotes
<section id="footnote-1"><sup>1 </sup>The grammar files are taken from grammar-v4, with the most recent modifications in <a href="https://github.com/antlr/grammars-v4/tree/fa4aff92b58e40bd337ab9f27217dc3feafbc32e/cpp">commit 6e10f7e</a> from May 2021.</section>