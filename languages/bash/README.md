# JPlag Bash language module

The JPlag Bash module allows the use of JPlag with submissions in Bash (shell scripts). <br>
It is based on a custom AI-generated ANTLR4 grammar. No established public ANTLR4 grammar for Bash exists in the [antlr/grammars-v4](https://github.com/antlr/grammars-v4) collection at this time.

### Bash specification compatibility

The grammar is supposed to cover most of the POSIX standard together with typical "bashisms" like command substition, associative arrays, etc. The full bash specification is considered out of scope, contributions are welcome.

### Token Extraction

The choice of tokens is intended to be similar to other language modules. Specifically, it includes:
- Function declarations and their bodies
- Control flow structures (if, for, while, until, case, select) with distinguished begin/end tokens
- Variable declarations and assignments
- Pipeline and redirection operators
- Command invocations and arguments
- Array and arithmetic expressions
- Subshell and command substitution blocks

The tokens should cover the POSIX standard as well as most common bash-specific features as of Bash 5.3

### Usage

To use the Bash module, add the `bash` module as a dependency and use the language identifier `bash`.

### Supported File Extensions

`.sh`, `.bash`

### Tests

There are two complementary test sources:

- `src/test/resources/de/jplag/bash/complete.sh`: a focused coverage fixture that demonstrates all currently supported syntax categories.
- Upstream [GNU bash repository](https://cgit.git.savannah.gnu.org/cgit/bash.git) tests: broad real-world corpus validation.

When using upstream tests, extract syntactically valid scripts before running JPlag:

```bash
cd /path/to/gnu-bash/tests
tmpdir=/tmp/bash-valid-tests
rm -rf "$tmpdir"
mkdir -p "$tmpdir"
for f in ./*.sh; do
    bash -n "$f" >/dev/null 2>&1 && cp --parents "$f" "$tmpdir"
done

# analyze syntactically valid tests
java -jar cli/target/jplag-*-jar-with-dependencies.jar -l bash ${tmpdir}
```

The upstream test cases still include unsupported syntax, therefore some ANTLR errors are expected.