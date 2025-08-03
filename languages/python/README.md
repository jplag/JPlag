# Python Tree-Sitter Language Module

This module provides Python language support for JPlag using the Tree-sitter parsing library. It replaces the previous ANTLR-based Python parser with a more robust and maintainable Tree-sitter implementation that supports modern Python syntax features.

## Overview

The Python Tree-sitter module consists of several key components that work together to parse Python source code and extract tokens for plagiarism detection:

- **TreeSitterPython**: Native language grammar loader
- **PythonParserAdapter**: Main parsing orchestration
- **PythonTokenCollector**: Token extraction from AST
- **PythonTokenType**: Language-specific token definitions
- **PythonLanguage**: JPlag language interface

## Architecture

### Core Components

#### TreeSitterPython
```java
public class TreeSitterPython extends TreeSitterLanguage {
    private static final TreeSitterPython INSTANCE = new TreeSitterPython();
    private static final String SYMBOL_NAME = "tree_sitter_python";
}
```

**Design Decisions:**
- **Singleton Pattern**: Ensures proper native library management
- **Symbol Naming**: Follows Tree-sitter convention `tree_sitter_<language>`

#### PythonParserAdapter
```java
public class PythonParserAdapter extends AbstractTreeSitterParserAdapter {
    @Override
    protected MemorySegment getLanguageMemorySegment() {
        return TreeSitterPython.language();
    }
}
```

**Design Decisions:**
- **Inheritance**: Extends `AbstractTreeSitterParserAdapter` for common parsing workflow
- **Separation of Concerns**: Delegates token extraction to specialized collector

## Token Extraction Implementation

### PythonTokenCollector Design

The `PythonTokenCollector` implements the `TreeSitterVisitor` interface and uses a sophisticated handler-based approach for token extraction.

#### Handler Map Pattern

```java
private final Map<String, Consumer<Node>> enterHandlers = new HashMap<>();
private final Map<String, Consumer<Node>> exitHandlers = new HashMap<>();
```

**Technical Insights:**
- **Efficient Dispatch**: O(1) lookup time for node type handlers
- **Separation of Concerns**: Clear distinction between enter and exit processing
- **Extensibility**: Easy to add new node types without modifying core logic

#### Node Type Mapping Strategy

The collector maps Tree-sitter node types to JPlag token types using a comprehensive mapping strategy:

```java
enterHandlers.put("class_definition", node -> addToken(PythonTokenType.CLASS_BEGIN, node));
enterHandlers.put("function_definition", node -> addToken(PythonTokenType.METHOD_BEGIN, node));
enterHandlers.put("if_statement", node -> addToken(PythonTokenType.IF_BEGIN, node));
```

### Position Calculation

The module implements position calculation for accurate token placement:

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

The module uses a hybrid approach for token length calculation:

```java
private int getTokenLength(PythonTokenType tokenType, Node node) {
    return switch (tokenType) {
        case IMPORT -> 6; // "import"
        case CLASS_BEGIN, CLASS_END -> 5; // "class"
        case METHOD_BEGIN, METHOD_END -> 3; // "def"
        case IF_BEGIN, IF_END, APPLY -> node.getEndByte() - node.getStartByte();
        default -> node.getEndByte() - node.getStartByte();
    };
}
```

**Design Rationale:**
- **Keyword Consistency**: Hardcoded lengths for Python keywords ensure consistency
- **Variable Length Support**: Calculated lengths for varying tokens, such as function calls

### End Token Positioning

The module implements special handling for end tokens to ensure proper nesting representation:

```java
private boolean isEndToken(PythonTokenType tokenType) {
    return switch (tokenType) {
        case CLASS_END, METHOD_END, WHILE_END, FOR_END, TRY_END, 
             EXCEPT_END, FINALLY_END, IF_END, DECORATOR_END, 
             WITH_END, MATCH_END, EXCEPT_GROUP_END -> true;
        default -> false;
    };
}
```

## Python-Specific Features

### Modern Python Support

The module supports modern Python features:

#### Match Statements (Python 3.10+)
```java
enterHandlers.put("match_statement", node -> addToken(PythonTokenType.MATCH_BEGIN, node));
enterHandlers.put("case_clause", node -> addToken(PythonTokenType.CASE, node));
```

#### Type Aliases (Python 3.12+)
```java
enterHandlers.put("type_alias_statement", node -> addToken(PythonTokenType.TYPE_ALIAS, node));
```

## Performance Characteristics

- **Native Library**: Efficient C-based parsing with minimal Java overhead
- **Token Collection**: ArrayList-based collection for O(1) append operations
- **Position Calculation**: Linear time complexity
- **Incremental Parsing**: Tree-sitter's incremental parsing capabilities

## Error Handling

### Parsing Failures
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

## Testing Strategy

### Unit Tests
- **Token Collector**: Tests individual token extraction logic
- **Position Calculation**: Validates line/column accuracy
- **Handler Mapping**: Ensures correct node type to token mapping

### Integration Tests
- **End-to-End Parsing**: Complete parsing workflow validation
- **Token Sequence**: Verifies correct token ordering and structure
- **File Handling**: Tests with various file formats and encodings

### Regression Tests
- **Compatibility**: Ensures consistency with ANTLR-based implementation
- **Performance**: Validates parsing performance characteristics
- **Edge Cases**: Tests with malformed code and large files

## Migration from ANTLR

### Key Differences
1. **Native Parsing**: Tree-sitter provides more robust parsing with better error recovery
2. **Modern Syntax**: Support for Python 3.10+ features
3. **Performance**: Improved parsing performance for large codebases
4. **Maintenance**: Reduced maintenance burden with Tree-sitter's active development

### Compatibility
- **Token Types**: Maintains compatibility with existing token type definitions
- **API Interface**: Same public interface as ANTLR-based implementation
- **Integration**: Seamless integration with existing JPlag infrastructure

## Future Enhancements

### Potential Improvements
1. **Caching**: Implement file content caching to avoid re-reading
2. **Parallel Processing**: Leverage Tree-sitter's incremental parsing for parallel processing
3. **Advanced Features**: Support for additional Python language features
4. **Performance Optimization**: Further optimization of position calculation algorithms

### Extensibility
- **New Node Types**: Easy addition of new Tree-sitter node types
- **Custom Tokens**: Flexible token type definition system
- **Language Features**: Extensible design for future Python language features

## References

- [Tree-sitter Documentation](https://tree-sitter.github.io/tree-sitter/)
- [Tree-sitter Python Grammar](https://github.com/tree-sitter/tree-sitter-python)
- [Python Language Reference](https://docs.python.org/3/reference/)
- [JPlag Language Module Design](docs/4.-Adding-New-Languages.md)
