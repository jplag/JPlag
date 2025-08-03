# Adding Tree-Sitter-Based Language Modules to JPlag

This guide explains how to add support for new programming languages to JPlag using the Tree-sitter parsing library. Tree-sitter provides robust, incremental parsing with excellent error recovery and support for many programming languages. This approach follows the same principles as ANTLR-based language modules but uses Tree-sitter's native parsing capabilities.

## Overview

Tree-sitter-based language modules in JPlag follow a similar structure to ANTLR-based modules but use Tree-sitter's native parsing capabilities. The key components are:

- **Tree-sitter Language Grammar**: Native library containing the language grammar
- **Language Implementation**: Java class that loads the native grammar
- **Parser Adapter**: Extends `AbstractTreeSitterParserAdapter` to handle parsing
- **Token Collector**: Implements `TreeSitterVisitor` to extract tokens from the AST
- **Token Types**: Enum defining language-specific token types

## Prerequisites

Before creating a new Tree-sitter-based language module, ensure:

1. Tree-sitter grammar exists for your target language
2. The grammar is available as a native library (typically `.so`, `.dylib`, or `.dll`)
3. You have access to the grammar's C API symbol (e.g., `tree_sitter_python`)

## Language Module Structure

A Tree-sitter-based language module consists of these parts:

| Component/Class                     | Superclass                           | Function                                   | How to get there                                        |
| ----------------------------------- | ------------------------------------ | ------------------------------------------ | ------------------------------------------------------- |
| Language class                      | `de.jplag.Language`                  | access point for the language module       | copy with small adjustments                             |
| `pom.xml`                           | -                                    | Maven submodule descriptor                 | copy with small adjustments; add dependencies for parser |
| `README.md`                         | -                                    | documentation for the language module      | copy for consistent structure; adjust from there        |
| TokenType class                     | `de.jplag.TokenType`                 | contains the language-specific token types | **implement new**                                       |
| Tree-sitter Language class          | `de.jplag.treesitter.TreeSitterLanguage` | loads native language grammar           | **implement new**                                       |
| ParserAdapter class                 | `de.jplag.treesitter.AbstractTreeSitterParserAdapter` | sets up Parser and calls Traverser | copy with small adjustments                             |
| TokenCollector class                | `de.jplag.treesitter.TreeSitterVisitor` | creates tokens traversing the AST        | **implement new**                                       |

For example, if Tree-sitter is used, the setup is as follows:

| Tree-sitter specific parts/files | Superclass                           | Function                           | How to get there                                                                             |
| --------------------------------- | ------------------------------------ | ---------------------------------- | -------------------------------------------------------------------------------------------- |
| Native library                    | -                                    | contains language grammar           | typically available from [Tree-sitter grammar repository](https://github.com/tree-sitter/tree-sitter) |
| Tree-sitter Language class        | `TreeSitterLanguage`                 | loads native grammar                | **implement new**                                                                             |
| TokenCollector class              | `TreeSitterVisitor`                  | creates tokens when called          | **implement new**                                                                             |
| ParserAdapter class               | `AbstractTreeSitterParserAdapter`    | sets up Parser and calls Traverser | copy with small adjustments                                                                  |

As the table shows, much of a Tree-sitter language module can be reused, especially when using the provided utilities. The only parts left to implement specifically for each language module are:
- the Tree-sitter Language class (for custom grammars)
- the TokenTypes, and
- the TokenCollector.

## Step-by-Step Implementation

### 1. Add Native Library Type

First, add your language's native library to the `NativeLibraryType` enum in `language-tree-sitter-utils`:

```java
public enum NativeLibraryType {
    // ... existing types ...
    
    /**
     * Your language grammar for Tree-sitter
     */
    TREE_SITTER_YOUR_LANGUAGE("tree-sitter-your-language", "1.0.0");
}
```

### 2. Create Language Implementation

Create a class that extends `TreeSitterLanguage` to load your language's grammar:

```java
package de.jplag.yourlanguage;

import java.lang.foreign.MemorySegment;
import de.jplag.treesitter.TreeSitterLanguage;
import de.jplag.treesitter.library.NativeLibraryType;

public class TreeSitterYourLanguage extends TreeSitterLanguage {
    private static final TreeSitterYourLanguage INSTANCE = new TreeSitterYourLanguage();
    private static final String SYMBOL_NAME = "tree_sitter_your_language";

    private TreeSitterYourLanguage() {
    }

    public static MemorySegment language() {
        return INSTANCE.call();
    }

    @Override
    protected NativeLibraryType libraryType() {
        return NativeLibraryType.TREE_SITTER_YOUR_LANGUAGE;
    }

    @Override
    protected String symbolName() {
        return SYMBOL_NAME;
    }
}
```

**Important**: Use the singleton pattern to ensure proper native library management and prevent memory leaks.

### 3. Define Token Types

Create an enum that implements `TokenType` for your language-specific tokens:

```java
package de.jplag.yourlanguage;

import de.jplag.TokenType;

public enum YourLanguageTokenType implements TokenType {
    // Basic constructs
    CLASS_BEGIN("CLASS_BEGIN"),
    CLASS_END("CLASS_END"),
    METHOD_BEGIN("METHOD_BEGIN"),
    METHOD_END("METHOD_END"),
    
    // Control flow
    IF_BEGIN("IF_BEGIN"),
    IF_END("IF_END"),
    WHILE_BEGIN("WHILE_BEGIN"),
    WHILE_END("WHILE_END"),
    
    // Language-specific constructs
    YOUR_SPECIAL_CONSTRUCT("YOUR_SPECIAL_CONSTRUCT");

    private final String description;

    YourLanguageTokenType(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isExcludedFromMatching() {
        return false; // Override for tokens that should be excluded
    }
}
```

### 4. Implement Token Collector

Create a token collector that implements `TreeSitterVisitor` to extract tokens from the AST:

```java
package de.jplag.yourlanguage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.Token;
import de.jplag.treesitter.TreeSitterVisitor;
import io.github.treesitter.jtreesitter.Node;

public class YourLanguageTokenCollector implements TreeSitterVisitor {
    private final List<Token> tokens;
    private final File file;
    private final String code;

    private final Map<String, Consumer<Node>> enterHandlers = new HashMap<>();
    private final Map<String, Consumer<Node>> exitHandlers = new HashMap<>();

    public YourLanguageTokenCollector(File file, String code) {
        tokens = new ArrayList<>();
        this.file = file;
        this.code = code;
        initializeHandlers();
    }

    private void initializeHandlers() {
        // Map Tree-sitter node types to token creation handlers
        enterHandlers.put("class_definition", node -> addToken(YourLanguageTokenType.CLASS_BEGIN, node));
        enterHandlers.put("function_definition", node -> addToken(YourLanguageTokenType.METHOD_BEGIN, node));
        enterHandlers.put("if_statement", node -> addToken(YourLanguageTokenType.IF_BEGIN, node));
        
        // Add exit handlers for balanced constructs
        exitHandlers.put("class_definition", node -> addToken(YourLanguageTokenType.CLASS_END, node));
        exitHandlers.put("function_definition", node -> addToken(YourLanguageTokenType.METHOD_END, node));
        exitHandlers.put("if_statement", node -> addToken(YourLanguageTokenType.IF_END, node));
    }

    @Override
    public void enter(Node node) {
        String nodeType = node.getType();
        Consumer<Node> handler = enterHandlers.get(nodeType);
        if (handler != null) {
            handler.accept(node);
        }
    }

    @Override
    public void exit(Node node) {
        String nodeType = node.getType();
        Consumer<Node> handler = exitHandlers.get(nodeType);
        if (handler != null) {
            handler.accept(node);
        }
    }

    private void addToken(YourLanguageTokenType tokenType, Node node) {
        // Calculate position and length
        int line = getLineNumber(node.getStartByte());
        int column = getColumnNumber(node.getStartByte());
        int length = getTokenLength(tokenType, node);
        
        tokens.add(new Token(tokenType, file, line, column, length, null));
    }

    // Implement position calculation methods...
    private int getLineNumber(int byteOffset) {
        // Count newlines up to the offset
        int line = 1;
        for (int i = 0; i < byteOffset && i < code.length(); i++) {
            if (code.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    private int getColumnNumber(int byteOffset) {
        // Find the last newline before the offset
        int lastNewlinePosition = -1;
        for (int i = 0; i < byteOffset && i < code.length(); i++) {
            if (code.charAt(i) == '\n') {
                lastNewlinePosition = i;
            }
        }
        return byteOffset - lastNewlinePosition;
    }

    private int getTokenLength(YourLanguageTokenType tokenType, Node node) {
        // Return hardcoded lengths for keywords or calculate from node span
        return switch (tokenType) {
            case CLASS_BEGIN, CLASS_END -> 5; // "class"
            case METHOD_BEGIN, METHOD_END -> 3; // "def" or "function"
            default -> node.getEndByte() - node.getStartByte();
        };
    }

    public List<Token> getTokens() {
        return new ArrayList<>(tokens);
    }
}
```

### 5. Create Parser Adapter

Implement a parser adapter that extends `AbstractTreeSitterParserAdapter`:

```java
package de.jplag.yourlanguage;

import java.io.File;
import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.nio.file.Files;
import java.util.List;

import de.jplag.Token;
import de.jplag.treesitter.AbstractTreeSitterParserAdapter;
import de.jplag.treesitter.TreeSitterTraversal;
import io.github.treesitter.jtreesitter.Node;

public class YourLanguageParserAdapter extends AbstractTreeSitterParserAdapter {
    @Override
    protected MemorySegment getLanguageMemorySegment() {
        return TreeSitterYourLanguage.language();
    }

    @Override
    protected List<Token> extractTokens(File file, Node rootNode) {
        try {
            String code = Files.readString(file.toPath());
            YourLanguageTokenCollector collector = new YourLanguageTokenCollector(file, code);
            TreeSitterTraversal.traverse(rootNode, collector);
            List<Token> tokens = collector.getTokens();
            tokens.add(Token.fileEnd(file));
            return tokens;
        } catch (IOException exception) {
            throw new RuntimeException("Failed to read file: " + file.getName(), exception);
        }
    }
}
```

### 6. Create Language Class

Create the main language class that implements the `Language` interface:

```java
package de.jplag.yourlanguage;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

@MetaInfServices(Language.class)
public class YourLanguageLanguage implements Language {
    @Override
    public String[] suffixes() {
        return new String[] {".your_ext"}; // File extensions for your language
    }

    @Override
    public String getName() {
        return "Your Language";
    }

    @Override
    public String getIdentifier() {
        return "yourlanguage";
    }

    @Override
    public int minimumTokenMatch() {
        return 12; // Adjust based on your language's characteristics
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new YourLanguageParserAdapter().parse(files);
    }
}
```

### 7. Configure Maven Build

Add your language module to the Maven build system:

1. Create a `pom.xml` for your language module
2. Add it to the parent `pom.xml` as a submodule
3. Ensure the native library is properly bundled

### 8. Integration into JPlag

After implementing your Tree-sitter language module, you need to integrate it into the JPlag system:

#### Maven Integration

1. Add your language module to the parent `pom.xml` as a submodule
2. Ensure the native library is properly bundled in the Maven build
3. Add any necessary dependencies to your module's `pom.xml`

#### CLI Integration

The language module will be automatically discovered by JPlag's service loader mechanism if you use the `@MetaInfServices(Language.class)` annotation on your language class.

#### Report Viewer Integration

To ensure your language gets properly registered and its code is correctly highlighted in the report-viewer:

1. Add your language to the `ParserLanguage` enum in 'src/model/Language.ts'. As the value for the entry, use its language module name.
2. Add your language to the switch-case in `src/utils/CodeHighlighter.ts` and return the correct highlight.js name. If your language is not supported by default, also register the language here.

#### Testing Integration

1. Create comprehensive unit tests for your token collector and parser adapter
2. Add integration tests to verify the complete parsing workflow
3. Include regression tests to ensure compatibility with existing functionality
4. Test with edge cases like malformed code and large files

## Token Selection

The choice of which Tree-sitter node types to convert into tokens is crucial for the effectiveness of plagiarism detection. The goal is to capture the structural essence of the code while being robust against simple obfuscation attempts.

### Basic Token Types

Most Tree-sitter-based language modules should include tokens for:

- **Class and function definitions**: `CLASS_BEGIN`, `CLASS_END`, `METHOD_BEGIN`, `METHOD_END`
- **Control flow structures**: `IF_BEGIN`, `IF_END`, `WHILE_BEGIN`, `WHILE_END`, `FOR_BEGIN`, `FOR_END`
- **Exception handling**: `TRY_BEGIN`, `TRY_END`, `EXCEPT_BEGIN`, `EXCEPT_END`
- **Language-specific constructs**: Import statements, assignments, function calls

### Token Extraction Strategy

The token extraction process follows these principles:

1. **Structural Focus**: Extract tokens that represent the program's structure rather than lexical elements
2. **Balanced Pairs**: For block constructs, generate both BEGIN and END tokens to maintain nesting information
3. **Language-Specific Features**: Include tokens for language-specific constructs that are important for plagiarism detection

## Key Design Patterns

### Handler Map Pattern

The token collector uses a map-based handler pattern to efficiently dispatch node types to appropriate token creation methods:

```java
private final Map<String, Consumer<Node>> enterHandlers = new HashMap<>();
private final Map<String, Consumer<Node>> exitHandlers = new HashMap<>();
```

This pattern allows for:
- Efficient O(1) lookup of handlers
- Easy addition of new node types
- Clear separation of enter/exit logic

### Singleton Pattern for Language Loading

Each Tree-sitter language implementation uses the singleton pattern to ensure:
- Proper native library management
- Consistent grammar loading

### Visitor Pattern

The `TreeSitterVisitor` interface enables:
- Clean separation of traversal logic from token extraction
- Reusable traversal utilities
- Consistent AST processing across languages

## Language Module Testing

To check the output of your Tree-sitter language module against the input, the `TokenPrinter` can be helpful. The `TokenPrinter` prints the input line by line, and the tokens of each line below it.

```
10 public class Example {
   |CLASS
    
11      private int number;
        |FIELD 
        
12      public int getNumber() {
        |METHOD
            
13          return number;
            |RETURN
       
14      }
        |}METHOD
   
15 }
   |}CLASS
```

To test a language module, set up a JUnit test class where the `TokenPrinter` prints the output of the `parse` method of the language module. Read through the output and check whether the `List<Token>` satisfies the given requirements.

### Test Files

The language module should be tested with 'authentic' sample code as well as a 'complete' test file that covers all syntactic elements that the language module should take into account. For Tree-sitter modules, you can find example grammars and test files in the [Tree-sitter grammar repository](https://github.com/tree-sitter/tree-sitter).

### Sanity Check Suggestions

- The token list represents the input code correctly.
  - In particular, the nesting tokens are correctly nested and balanced.
- The token list represents the input code with an acceptable coverage —how that can be measured and what coverage is acceptable depends on the language. One approach would be line coverage, e.g. 90 percent of code lines should contain a token.
- There are no `TokenTypes` that can never be produced by the language module for any input.
  - Put another way, the complete test code produces a token list that contains every type of token.

### Writing Tests Using the Test API

The language-testutils module provides a simple way to implement tests for language modules. To use that, you have to make your test class extend `LanguageModuleTest`:

```java
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

public class YourLanguageTest extends LanguageModuleTest {
    public YourLanguageTest() {
        super(new YourLanguageLanguage(), Arrays.asList(YourLanguageTokenType.values()));
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        // Configure test sources and test types
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        // Configure lines to ignore in coverage tests
    }
}
```

In the 'collectTestData' method, you can configure the test sources that you want to test and what kind of test should be run:

```java
collector.testFile("firstFile.py", "secondFile.py").testSourceCoverage().testContainedTokens(YourLanguageTokenType.CLASS_BEGIN);
collector.inlineSource("def main():\n    pass").testCoverages();
```

The 'configureIgnoredLines' method can be used to exclude lines from the source coverage check:

```java
collector.ignoreLinesByPrefix("#"); // Ignore Python comments
collector.ignoreMultipleLines("'''", "'''"); // Ignore multi-line strings
```

## Troubleshooting

### Common Issues

1. **Native library not found**: Ensure the library is properly bundled and the symbol name is correct
2. **Parsing failures**: Check that your Tree-sitter grammar supports the syntax you're testing
3. **Token positioning**: Debug line/column calculation for accurate token placement

### Debugging Tips

1. Use the `PythonTokenSequencePrinter` as a reference for debugging token extraction
2. Enable Tree-sitter debug output to see parsing details
3. Test with simple, well-formed code first before complex examples

## Example Implementation

See the Python Tree-sitter implementation in `languages/python/` for a complete working example of all these components.

## References

- [Tree-sitter Documentation](https://tree-sitter.github.io/tree-sitter/)
- [JPlag Language Module Design](https://github.com/jplag/JPlag/wiki/4.-Adding-New-Languages)
- [Python Tree-sitter Implementation](languages/python/)
- [Tree-sitter Utils](language-tree-sitter-utils/)
- [Tree-sitter Grammar Repository](https://github.com/tree-sitter/tree-sitter) 
