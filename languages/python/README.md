# Python Tree-Sitter Language Module

This module implements Python language support for JPlag using Tree-sitter. The implementation leverages Tree-sitter's native parsing capabilities to provide robust, high-performance Python code analysis while maintaining compatibility with JPlag's token-based plagiarism detection approach.

## Overview

The Python Tree-sitter module consists of several key components that work together to parse Python source code and extract tokens for plagiarism detection:

- **TreeSitterPython**: Native language grammar loader
- **PythonParserAdapter**: Main parsing orchestration and error handling
- **PythonTokenCollector**: Token extraction from AST using a handler-based approach
- **PythonTokenType**: Language-specific token definitions with embedded length information
- **PythonLanguage**: JPlag language interface implementation

## Architecture

### Core Components

#### TreeSitterPython
```java
public class TreeSitterPython extends TreeSitterLanguage {
    private static final TreeSitterPython INSTANCE = new TreeSitterPython();
    private static final String SYMBOL_NAME = "tree_sitter_python";
    
    public static MemorySegment language() {
        return INSTANCE.call();
    }
}
```

#### PythonParserAdapter
```java
public class PythonParserAdapter extends AbstractTreeSitterParserAdapter {
    @Override
    protected MemorySegment getLanguageMemorySegment() {
        return TreeSitterPython.language();
    }

    @Override
    protected List<Token> extractTokens(File file, Node rootNode) {
        String code;

        try {
            code = Files.readString(file.toPath());
        } catch (IOException exception) {
            throw new RuntimeException("Failed to read file: " + file.getName(), exception);
        }

        PythonTokenCollector collector = new PythonTokenCollector(file, code);
        TreeSitterTraversal.traverse(rootNode, collector);
        List<Token> tokens = collector.getTokens();
        tokens.add(Token.fileEnd(file));
        return tokens;
    }
}
```

## Token Extraction Implementation

### PythonTokenCollector Design

The `PythonTokenCollector` implements the `TreeSitterVisitor` interface to traverse the AST in a depth-first search approach for token extraction.

#### Handler Map

```java
private final Map<String, Consumer<Node>> enterHandlers = new HashMap<>();
private final Map<String, Consumer<Node>> exitHandlers = new HashMap<>();
```

#### Node Type Mapping Strategy

The collector maps Tree-sitter node types to JPlag token types using a comprehensive mapping strategy:

```java
enterHandlers.put("class_definition", node -> addToken(PythonTokenType.CLASS_BEGIN, node));
enterHandlers.put("function_definition", node -> addToken(PythonTokenType.METHOD_BEGIN, node));
enterHandlers.put("if_statement", node -> addToken(PythonTokenType.IF_BEGIN, node));
```

### Position Calculation

Tree-sitter provides byte offsets, but JPlag needs line and column positions. The module implements custom position calculation for this.

#### Line Number Calculation

```java
private int getLineNumber(int byteOffset) {
    int line = 1;
    for (int i = 0; i < byteOffset && i < code.length(); i++) {
        if (isNewline(i)) {
            line++;
        }
    }
    return line;
}
```

#### Column Number Calculation

```java
private int getColumnNumber(int byteOffset) {
    int lastNewlinePosition = -1;
    for (int i = 0; i < byteOffset && i < code.length(); i++) {
        if (isNewline(i)) {
            lastNewlinePosition = i;
        }
    }
    return byteOffset - lastNewlinePosition;
}
```

### Token Length Strategy

The module uses Tree-sitter's byte offsets for accurate token length calculation:

```java
private int getTokenLength(PythonTokenType tokenType, Node node) {
    int length = tokenType.getLength();
    if (length == -1) {
        // Dynamic length tokens calculated from Tree-sitter's precise byte offsets
        return node.getEndByte() - node.getStartByte();
    }
    return length;
}
```

### End Token Positioning

The module implements handling for end tokens to ensure proper nesting representation:

```java
private boolean isEndToken(PythonTokenType tokenType) {
    return switch (tokenType) {
        case CLASS_END, METHOD_END, FOR_END -> true;
        default -> false;
    };
}
```

## Python-Specific Features

### Modern Python Support

Tree-sitter's Python grammar provides native support for modern Python features that were challenging with ANTLR:

#### Match Statements (Python 3.10+)

```java
enterHandlers.put("match_statement", node -> addToken(PythonTokenType.MATCH_BEGIN, node));
enterHandlers.put("case_clause", node -> addToken(PythonTokenType.CASE, node));
```

#### Exception Groups (Python 3.11+)

```java
enterHandlers.put("except_group_clause", node -> addToken(PythonTokenType.EXCEPT_GROUP_END, node));
```

#### Type Aliases (Python 3.12+)

```java
enterHandlers.put("type_alias_statement", node -> addToken(PythonTokenType.TYPE_ALIAS, node));
```

## Error Handling 

### Parsing

```java
Tree tree = parser.parse(code, InputEncoding.UTF_8)
    .orElseThrow(() -> new ParsingException(file, "Tree-sitter failed to parse file: " + file.getName()));
```

### File I/O Exceptions
```java
try {
    String code = Files.readString(file.toPath());
    // ... processing
} catch (IOException exception) {
    throw new RuntimeException("Failed to read file: " + file.getName(), exception);
}
```

## Extensibility

- **New Node Types**: Easy addition of new Tree-sitter node types with handler registration
- **Custom Tokens**: Flexible token type definition system with embedded length information
- **Language Features**: Extensible design for future Python language features as they're added to Tree-sitter

## References

- [Tree-sitter Documentation](https://tree-sitter.github.io/tree-sitter/)
- [Tree-sitter Python Grammar](https://github.com/tree-sitter/tree-sitter-python)
- [Python Language Reference](https://docs.python.org/3/reference/)
- [JPlag Language Module Design](docs/4.-Adding-New-Languages.md)
