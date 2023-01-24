# JPlag Rust language module

The JPlag Rust module allows the use of JPlag with submissions in Scala. <br>
It is based on the [Rust ANTLR4 grammar](https://github.com/antlr/grammars-v4/tree/master/rust), licensed under MIT.

### Rust specification compatibility

According to the grammar's documentation, it was updated to Rust 1.60.0 (April 2022).

### Token Extraction

#### General

The choice of tokens is intended to be similar to the Java or C# modules. Specifically, among others, it includes a
range of nesting structures (class and method declarations, control flow expressions) as well as variable declaration,
object creation, assignment, and control flow altering keywords. <br>
Blocks are distinguished by their context, i.e. there are separate `TokenConstants` for `if` blocks, `for` blocks, class
bodies, method bodies, array constructors, and the like.

More syntactic elements of Rust may turn out to be helpful to include in the future, especially those that are newly
introduced.

#### Problem in Rust (1): Grammar formulation

In contrast to other grammars used in modules, the underlying Rust ANTLR4 grammar uses very general syntactic categories 
that do not provide very much _semantic_ information. For example, the ifExpression rule features a `blockExpression` as
its body instead of a separate `ifBody` rule. This makes it hard to differentiate different uses of those `blockExpression`s.

It should be possible to refactor the grammar to include more specific rules. While not hard, this will still be tedious. Most of the
`ParserState` mechanism should become obsolete if this is done.

#### Problem in Rust (2): Pattern resolution

Rust allows to destruct complex objects using pattern matching.

```rust
// assigns a = 1; b = 2; c = 5;
let (a, b,.., c) = (1, 2, 3, 4, 5);

// assigns d = tuple[0]; f = tuple[n-1]
let (d,.., f) = tuple;
```

The _patterns_ on the left hand side as well as the elements on the right hand side can be nested freely. The _rest_
or _etcetera_ pattern `..` is used to skip a number of elements, so that the elements following it match the end part of
the assigned object.

These `let` pattern assignments can be replaced with a sequence of more basic assignments. This is a possible
problem of this module.

#### Problem in Rust (3): `return` is optional

In Rust, the `return` keyword is optional. If omitted, the last expression evaluated in the function body is used as the
return value.

```rust
fn power(base: i32, exponent: i32) -> i32 {
    if exponent == 0 { 1 }                              // mark this return value?
    else if exponent == 1 { base }                      // and this one?
    else if exponent % 2 == 0 {
        let square = |i: i32| { i * i };
        square(power(base, exponent / 2))               // and this one?
    } else {
        base * power(base, exponent - 1)                // and this one?
    }
}
```

That raises the question whether to try and mark these more implicit return values, so that the output of this module
would be consistent with others.

To determine all possible return values, semantic information about control structures is necessary which may be tedious
to extract from the AST, but possible (e.g. by means of a stack mechanic).
On the other hand, "the last expression of a block evaluated" does not hold the same _syntactical_ weight to it as a
return
statement.

For the moment, implicit block values get no special tokens.

#### Problem in Rust (4): Macros

Macros are a vital part of Rust. They allow to expand brief statements into more complex, repeating code at compile time.

The expansion of the macro arguments into the macro code and the expansion of the macro code itself are purely textual, so a Rust parser does not parse their syntax (apart from the bracket structure). This makes it hard to generate meaningful tokens for them.

Currently, macro rule definition bodies and macro macro invocation arguments/bodies get no tokens.

### Usage

To use the Rust module, add the `-l rust` flag in the CLI, or use a `JPlagOption` object
with `new de.jplag.rust.Language()` as `language` in the Java API as described in the usage information in
the [readme of the main project](https://github.com/jplag/JPlag#usage)
and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).
