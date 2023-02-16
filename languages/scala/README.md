# JPlag Scala language module

The JPlag Scala module allows the use of JPlag with submissions in Scala. <br>
It is based on the [Scalameta library](https://scalameta.org/) parser, and is adapted from
the [CodeGra-de Scala module](https://github.com/CodeGra-de/jplag/tree/master/jplag.module.scala) for JPlag, both
licensed under BSD-3.

### Scala specification compatibility

The dependencies only allow compatibility up to Scala 2.13.8 (January 2022), so the new syntactical features of Scala 3 
are not supported yet.

As of now, Scalameta is not available for Scala 3 yet (see the [GitHub issue](https://github.com/scalameta/scalameta/issues/2485)), 
so the upgrade needs to wait. It would seem that once this module is equipped with Scalameta for Scala 3, it will be able to handle both Scala 2 and 3 equally as [the syntax is backwards compatible](https://scala-lang.org/2019/12/18/road-to-scala-3.html#:~:text=Scala%203%20is%20backwards%20compatible%20with%20Scala%202) for the most part.

### Token Extraction

#### General

The choice of tokens is intended to be similar to the Java or C# modules. Specifically, among others, it includes a
range of nesting structures (class and method declarations, control flow expressions) as well as variable declaration,
object creation, assignment, and control flow altering keywords. <br>
Blocks are distinguished by their context, i.e. there are separate `TokenTypes` for `if` blocks, `for` blocks, class
bodies, method bodies, array constructors, and the like.

More syntactic elements of Scala may turn out to be helpful to include in the future, especially those that are newly
introduced.

#### Problem in Scala (1): Method calls

The syntax of Scala allows to omit the parentheses when calling methods without arguments. These method calls are
indistinguishable from member references. This makes the system vulnerable to attacks where an empty set of brackets is
simply added after the member name. To address this, method calls with no arguments are not marked with an `APPLY`
token, even though they are recognizable as method calls.

```scala
myObject.member // may be member reference or method call
// gets MEMBER token

myObject.member2() // must be method call
// gets MEMBER token

myObject.member3(arg1, arg2) // must be method call
// gets APPLY MEMBER ARG ARG tokens
```

#### Problem in Scala (2): Operators

Operators are implemented as regular method calls. Additionally, custom operators on objects/classes can be defined,
possibly overloading existing ones like `+`, `&=` etc.

In other modules, operations are not assigned tokens but "regular" method calls are. This calls for the task to try to
distinguish operations from what we understand as "regular" method calls. This is not entirely possible with only
parsing information, so we decided to go about this problem as follows:

- Calls to methods with an identifier that is used as an operator are NOT treated as a method call. This is accomplished
  by comparing to a hard-coded list of standard operators on numbers, booleans, lists, and types (although type
  operators cannot be used in the same contexts as the others). This applies in infix and dot notation.
- Calls to methods with any other identifier, be it alphanumerical, symbolic or any combination, are treated as method
  calls and are assigned `APPLY` and `ARG` tokens if applicable, see (1).

#### Problem in Scala (3): `return` is optional and discouraged

In Scala, the use of the `return` keyword is regarded as a bad smell because it may disrupt the control flow in ways
unintended by the less experienced Scala developer.
Instead, like any block of code, the method body is evaluated to the last expression that is evaluated.

```scala
def power(base: Int, exponent: Int): Int = {
    if (exponent == 0) 1                                // mark this return value?
    else if (exponent == 1) base                        // and this one?
    else if (exponent % 2 == 0) 
        ((i: Int) => i*i)(power(base, exponent / 2))    // and this one?
    else base * power(base, exponent - 1)               // and this one?
}
```
That raises the question whether to try and mark these more implicit return values, so that the output of this module
would be consistent with others.

To determine all possible return values, semantic information about control structures is necessary which may be tedious
to extract from the AST, but possible (e.g. by means of a stack mechanic).
On the other hand, "the last expression of a block evaluated" does not hold the same _syntactical_ weight to it as a return
statement.

For the moment, implicit block values are neglected.

### Usage

To use the Scala module, add the `-l scala` flag in the CLI, or use a `JPlagOption` object
with `new de.jplag.scala.Language()` as `language` in the Java API as described in the usage information in
the [readme of the main project](https://github.com/jplag/JPlag#usage)
and [in the wiki](https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag).
