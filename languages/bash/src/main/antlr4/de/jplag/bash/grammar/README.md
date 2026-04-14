# Bash ANTLR 4 grammar

This grammar was written for use in JPlag's Bash language module.

No established public ANTLR4 grammar for Bash exists in the
[antlr/grammars-v4](https://github.com/antlr/grammars-v4) repository at this time.
This grammar was crafted specifically for the purpose of plagiarism detection
and covers the most commonly used Bash constructs.

Licensed under the same license as the JPlag project.

Entry rule is `program`.

## Supported Constructs

- Functions (`function name() { ... }` and `name() { ... }`)
- Control flow: `if/elif/else/fi`, `for/do/done`, `while/do/done`, `until/do/done`, `case/esac`, `select`
- Variable assignments (including `local`, `declare`, `readonly`, `export`)
- Pipelines and logical operators (`|`, `&&`, `||`)
- Redirections (`<`, `>`, `>>`, `<<`, `<<<`, etc.)
- Command substitution (`$(...)` and backticks)
- Arithmetic expressions (`$((...))` and `((...))`)
- Test expressions (`[[ ... ]]` and `[ ... ]`)
- Subshells (`(...)`)
- Brace groups (`{ ...; }`)
- String literals (single-quoted, double-quoted, ANSI-C strings)

## Known Limitations

- Bash is highly context-sensitive and not easily captured by a context-free grammar.
- Complex parameter expansions may not be fully parsed.
- Heredocs with complex content may not be fully handled.
- Aliases and some advanced Bash-specific features are not covered.
