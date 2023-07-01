# JPlag LLVM IR language module

The JPlag LLVM IR module allows the use of JPlag with submissions in the LLVM IR. <br>
It is based on the [LLVMIR ANTLR4 grammar](https://github.com/antlr/grammars-v4/tree/master/llvm-ir), licensed under MIT.

### LLVM IR specification compatibility

The grammar definition targets LLVM 15.

If the grammar is updated to a more recent<a href="#footnote-1"><sup>1</sup></a> syntax definition, this module should surely be updated as well.


### Token Extraction

TODO

### Usage

To use the LLVM IR module, add the `-l llvmir` flag in the CLI, or use a `JPlagOption` object with `new de.jplag.llvmir.Language()` as `language` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

<br>

#### Footnotes
<section id="footnote-1"><sup>1 </sup>The grammar files are taken from grammar-v4, with the most recent modification in <a href="https://github.com/antlr/grammars-v4/tree/fe8ee8e03ffc4af9270e430a17817d25480b72f5/llvm-ir">commit 9644ff9</a> from March 2023.</section>