# Adding Tree-Sitter-Based Language Modules to JPlag

This guide explains how to add support for new programming languages to JPlag using the Tree-sitter parsing library. Tree-sitter provides robust, incremental parsing with support for many programming languages.

## Language Module Structure

A Tree-sitter-based language module consists of these components:

| Component | Superclass/Interface | Function | Implementation |
|-----------|---------------------|---------|----------------|
| Language class | `de.jplag.Language` | Entry point for the language module | Copy and adapt from existing module |
| TokenType enum | `de.jplag.TokenType` | Language-specific token definitions | **Implement new** |
| Tree-sitter Language class | `de.jplag.treesitter.TreeSitterLanguage` | Loads native grammar | **Implement new** |
| Parser class | `de.jplag.treesitter.AbstractTreeSitterParser` | Sets up parser and calls traverser | Copy and adapt from existing module |
| TokenCollector class | `de.jplag.treesitter.TreeSitterVisitor` | Extracts tokens from AST | **Implement new** |

## Step-by-Step Implementation

### Step 1: Create the Module Structure

1. Create a new directory in `languages/` for your language (e.g., `languages/rust/`)
2. Copy the structure from an existing Tree-sitter module like `languages/python/`
3. Update the `pom.xml` with your language-specific dependencies

### Step 2: Add Native Library Support

First, add your language's native library to the `NativeLibraryType` enum in `language-tree-sitter-utils/src/main/java/de/jplag/treesitter/library/NativeLibraryType.java`:

```java
public enum NativeLibraryType {
    // ... existing types ...
    
    /**
     * Your language grammar for Tree-sitter
     */
    TREE_SITTER_YOUR_LANGUAGE("tree-sitter-your-language", "1.0.0");
}
```

### Step 3: Create the Tree-sitter Language Class

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
        // Private constructor for singleton pattern
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

### Step 4: Define Token Types

Create an enum that implements `TokenType` for your language-specific tokens:

```java
package de.jplag.yourlanguage;

import de.jplag.TokenType;

public enum YourLanguageTokenType implements TokenType {
    // Basic constructs
    CLASS_BEGIN("CLASS{", 5), // class
    CLASS_END("}CLASS", 7), // class end
    METHOD_BEGIN("METHOD{", 7), // method/function
    METHOD_END("}METHOD", 9), // method end
    
    // Add more tokens as needed
    ;

    private final String description;
    private final int length;

    YourLanguageTokenType(String description, int length) {
        this.description = description;
        this.length = length;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public int getLength() {
        return length;
    }
}
```

### Step 5: Implement Token Collector

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

public class YourLanguageTokenCollector extends TreeSitterVisitor {
    private final List<Token> tokens;
    private final File file;

    public YourLanguageTokenCollector(File file) {
        tokens = new ArrayList<>();
        this.file = file;
    }

    @Override
    protected void initializeHandlers() {
        // Map Tree-sitter node types to token creation handlers
        enterHandlers.put("class_definition", node -> addToken(YourLanguageTokenType.CLASS_BEGIN, node));
        enterHandlers.put("function_definition", node -> addToken(YourLanguageTokenType.METHOD_BEGIN, node));
        
        // Add exit handlers for balanced constructs
        exitHandlers.put("class_definition", node -> addToken(YourLanguageTokenType.CLASS_END, node));
        exitHandlers.put("function_definition", node -> addToken(YourLanguageTokenType.METHOD_END, node));
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

    private void addToken(YourLanguageTokenType tokenType, Node node, int length) {
        int startLine = node.getStartPoint().row() + 1;
        int startColumn = node.getStartPoint.column() + 1;
        int endLine = node.getEndPoint().row() + 1;
        int endColumn = startColumn + length;
        
        tokens.add(new Token(tokenType, file, startLine, startColumn, endLine, endColumn, length));
    }

    public List<Token> getTokens() {
        return new ArrayList<>(tokens);
    }
}
```

### Step 6: Create Parser Adapter

Implement a parser that extends `AbstractTreeSitterParser`:

```java
package de.jplag.yourlanguage;

import java.io.File;
import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.nio.file.Files;
import java.util.List;

import de.jplag.Token;
import de.jplag.treesitter.AbstractTreeSitterParser;
import io.github.treesitter.jtreesitter.Node;

public class YourLanguageParser extends AbstractTreeSitterParser {
    @Override
    protected MemorySegment getLanguageMemorySegment() {
        return TreeSitterYourLanguage.language();
    }

    @Override
    protected List<Token> extractTokens(File file, Node rootNode) {
        String code;
        
        try {
            code = Files.readString(file.toPath())
        } catch (IOException exception) {
            throw new RuntimeException("Failed to read file: " + file.getName(), exception);
        }

        YourLanguageTokenCollector collector = new YourLanguageTokenCollector(file, code);
        collector.traverse(rootNode);
        List<Token> tokens = collector.getTokens();
        tokens.add(Token.fileEnd(file));
        return tokens;
    }
}
```

### Step 7: Create Language Class

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
    public List<String> fileExtensions() {
        return List.of(".your_ext"); // File extensions for your language
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

### Step 8: Configure Maven Build

1. **Update your module's `pom.xml`:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>de.jplag</groupId>
        <artifactId>languages</artifactId>
        <version>${revision}</version>
    </parent>
    <artifactId>yourlanguage</artifactId>

    <dependencies>
        <dependency>
            <groupId>io.github.tree-sitter</groupId>
            <artifactId>jtreesitter</artifactId>
            <version>${tree-sitter.version}</version>
        </dependency>
        <dependency>
            <groupId>de.jplag</groupId>
            <artifactId>language-tree-sitter-utils</artifactId>
            <version>${revision}</version>
        </dependency>
    </dependencies>
</project>
```

2. **Add your module to the parent `pom.xml`** in the `languages` profile.

### Step 9: Build Native Libraries

Add your language to the native library build process:

1. **Update the build script** in `scripts/build-native-libraries.sh` to include your language
2. **Update the Maven profile** in the root `pom.xml` to build your language's native library

## Testing Your Language Module

Create a test class extending `LanguageModuleTest`:

```java
package de.jplag.yourlanguage;

import java.util.Arrays;
import de.jplag.testutils.LanguageModuleTest;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

public class YourLanguageTest extends LanguageModuleTest {
    public YourLanguageTest() {
        super(new YourLanguageLanguage(), Arrays.asList(YourLanguageTokenType.values()));
    }

    @Override
    protected void collectTestData(TestDataCollector collector) {
        // Add test files and configure test types
        collector.testFile("test1.your_ext", "test2.your_ext")
                .testSourceCoverage()
                .testContainedTokens(YourLanguageTokenType.CLASS_BEGIN);
        
        collector.inlineSource("class Test {\n    def method() {\n        pass\n    }\n}")
                .testCoverages();
    }

    @Override
    protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector) {
        // Configure lines to ignore in coverage tests
        collector.ignoreLinesByPrefix("//"); // Ignore single-line comments
        collector.ignoreMultipleLines("/*", "*/"); // Ignore multi-line comments
    }
}
```

## Integration into JPlag

### CLI Integration

Your language module will be automatically discovered by JPlag's service loader mechanism thanks to the `@MetaInfServices(Language.class)` annotation.

### Report Viewer Integration

To ensure proper code highlighting in the report viewer:

1. Add your language to the `ParserLanguage` enum in `report-viewer/src/model/Language.ts`
2. Add your language to the switch-case in `report-viewer/src/utils/CodeHighlighter.ts`

## Troubleshooting

### Common Issues

1. **Native library not found**: Ensure the library is properly bundled and the symbol name is correct
2. **Parsing failures**: Check that your Tree-sitter grammar supports the syntax you're testing
3. **Token positioning**: Debug line/column calculation for accurate token placement

### Debugging Tips

1. Use the `TokenPrinter` to visualize token extraction
2. Enable Tree-sitter debug output to see parsing details
3. Test with simple, well-formed code first before complex examples

## Example Implementation

See the Python Tree-sitter implementation in `languages/python/` for a complete working example of all these components.

## References

- [Tree-sitter Documentation](https://tree-sitter.github.io/tree-sitter/)
- [Tree-sitter Grammar Repository](https://github.com/tree-sitter/tree-sitter)
- [Tree-sitter Utils](language-tree-sitter-utils/) 
- [Python Tree-sitter Implementation](languages/python/)
