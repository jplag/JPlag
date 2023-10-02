# JPlag LLVM IR language module

The JPlag LLVM IR module allows the use of JPlag with submissions in the LLVM IR. <br>
It is based on the [LLVMIR ANTLR4 grammar](https://github.com/antlr/grammars-v4/tree/master/llvm-ir), licensed under MIT.

### LLVM IR specification compatibility

The grammar definition targets LLVM 15, released in September 2022.

The grammar in this repo contains a fix, see the comment in the [LLVM IR grammar](src/main/antlr4/de/jplag/llvmir/grammar/LLVMIR.g4).

If the grammar is updated to a more recent<a href="#footnote-1"><sup>1</sup></a> syntax definition, this module should surely be updated as well.


### Token Extraction

The choice of tokens includes nesting tokens for functions and basic blocks and separate tokens for various elements.
These include binary and bitwise instructions (like addition and or), memory operations (like load and store), terminator instructions (like branches), conversions, global variables, type definitions, constants and others.


### Usage

To use the LLVM IR module, add the `-l llvmir` flag in the CLI, or use a `JPlagOption` object with `new de.jplag.llvmir.LLVMIRLanguage()` as `language` in the Java API as described in the usage information in the [readme of the main project](https://github.com/jplag/JPlag#usage) and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).

We recommend using the [LLVM optimizer](https://llvm.org/docs/CommandGuide/opt.html) to optimize the LLVM IR code before using JPlag.
In our tests, optimization level 1 showed the best results in plagiarism detection quality and should therefore, be used.

### Minimum Token Match

It can be difficult to find a good value for the minimum token match because the range of possible candidates for low-level languages like the LLVM IR is much larger.
Values can range between 60 and 70 for code compiled from C to more than 1000 for code compiled from C++.
From our tests, we calculated a formula that depends on the average lines of code (avg. loc) to determine a value that should provide good results:

min_token_match(x) = 48.2055162 * e^(0.000333593799 * x)

with x = (avg. loc of the LLVM IR code) - (avg. loc of the source code), <br>
where the source code is the code from which the IR code was generated, for example, the C or C++ code.

<br>

#### Footnotes
<section id="footnote-1"><sup>1 </sup>The grammar files are taken from grammar-v4, with the most recent modification in <a href="https://github.com/antlr/grammars-v4/tree/768b12e1db509aa700a316e3eed1e23e8c4bdb06/llvm-ir">commit 768b12e</a> from August 2023.</section>