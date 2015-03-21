JPlag Frontend Pack

The JPlag system uses different frontends to transform the input files into a
token stream, which abstracts very much from the original source code. For
example in our Java frontend any assignments are translated to J_ASSIGN
tokens, method invocations become J_APPLY tokens, arithmetic calculations,
names, and comments are ignored, etc. Only some kind of program structure
remains, which is then compared to other source files.

This package contains a minimal set of files required to build our 
Java 1.4 frontend (including ANTLR 2.7.7). The jplag.Program class has been
reduced to only two functions.
The grammar is specified with ANTLR 2.7.7 (src/main/antlr/java.g). In 
some productions the parser adds tokens to the above mentioned token stream. 
Search for "parser.add(" to find these locations. The token types are defined
in "src/main/java/jplag/java/JavaToken.java" and "src/main/java/jplag/java/JavaTokenConstants.java".

After building the source with a simple "mvn compile", you can test the parser by 
invoking the main method of jplag.java.Parser with a Java file to parse as arguemtn:

java -cp "antlr.jar;build" jplag.java.Parser src/main/java/jplag/java/Parser.java

This will show you the listing of src/main/java/jplag/java/Parser.java on the right side 
of the screen with the generated tokens and their locations on the left side.

To generate a new frontend you have to do the following things:
- Generate an ANTLR 2.7.7 grammar (note: ANTLR 3 is quite different
  from ANTLR 2) -> this is done during mvn compile
- Choose which tokens should be generated
- Declare the token types in a ...Token.java and ...TokenConstants.java file
- Add the tokens with "parser.add(..." in the grammar
- Adapt the other files

I hope this helps you to understand, how our frontends work and to create 
your own frontend.

It is also possible to use another parser generator. Our Java 1.5 frontend
for example uses JavaCC 3.2. If you are interested in using JavaCC, we can
also provide you with the Java 1.5 frontend.