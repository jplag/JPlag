# JPlag TypeScript language module
Due to TypeScript being a superset of JavaScript this frontend can also parse JavaScript files.
<br>
The JPlag TypeScript module allows the use of JPlag with submissions in TypeScript. <br>
It is based on the [TypeScript ANTLR4 grammar](https://github.com/antlr/grammars-v4/tree/master/javascript/typescript), licensed under the Apache 2.0.


### TypeScript specification compatibility
> This TypeScript grammar does not exactly correspond to the TypeScript standard. The main goal during developing was practical usage, performance, and clarity (getting rid of duplicates).

Since the grammar has no support for decorators the version can be estimated < 5.0. The grammar can still parse files with decorators, but can not extract a tokens for them.
<br> The grammar can parse multiple language features from version 4.x.
<br> Because of this the version is still given as an estimated v5.

If there are any major updates or fixes to the grammar<a href="#footnote-1"><sup>1</sup></a>, they should surely be applied to this module as well.

### Token Extraction
The choice of tokens is intended to be similar to the Java or Python modules. It includes a range of nesting structures (class, method, control flow expressions) as well as variable declaration, object creation and assignment.

### Usage
To use the TypeScript module, use the `typescript` subcommand in the CLI, or use a `JPlagOption` object with `new de.jplag.typescript.TypeScriptLanguage()` as `language` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

#### Footnotes
<section id="footnote-1"><sup>1 </sup>The grammar files are taken from grammar-v4, with the most recent modification in <a href="https://github.com/antlr/grammars-v4/commit/764afe99457c07ae81ac07ed3a351a96fb5330d8">commit 768b12e</a> from March 2023.</section>