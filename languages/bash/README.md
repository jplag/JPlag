# JPlag Bash language module

The JPlag Bash module allows the use of JPlag with submissions in Bash (shell scripts). <br>
It is based on a custom ANTLR4 grammar written for this module. No established public ANTLR4 grammar for Bash exists in the [antlr/grammars-v4](https://github.com/antlr/grammars-v4) collection at this time.

### Bash specification compatibility

The grammar covers commonly used Bash constructs including functions, control flow (if/elif/else, for, while, until, case, select), variable assignments, command substitution, pipelines, redirections, and arithmetic expressions. It is not a complete implementation of the full POSIX shell or Bash specification.

### Token Extraction

The choice of tokens is intended to be similar to other language modules. Specifically, it includes:
- Function declarations and their bodies
- Control flow structures (if, for, while, until, case, select) with distinguished begin/end tokens
- Variable declarations and assignments
- Pipeline and redirection operators
- Command invocations and arguments
- Array and arithmetic expressions
- Subshell and command substitution blocks

Blocks are distinguished by their context, i.e. there are separate token types for `if` blocks, `for` blocks, `case` blocks, function bodies, and the like.

### Usage

To use the Bash module, add the `bash` module as a dependency and use the language identifier `bash`.

### Supported File Extensions

`.sh`, `.bash`
