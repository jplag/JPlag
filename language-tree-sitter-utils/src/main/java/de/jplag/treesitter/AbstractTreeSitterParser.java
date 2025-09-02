package de.jplag.treesitter;

import java.io.File;
import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.ParsingException;
import de.jplag.Token;

import io.github.treesitter.jtreesitter.InputEncoding;
import io.github.treesitter.jtreesitter.Language;
import io.github.treesitter.jtreesitter.Node;
import io.github.treesitter.jtreesitter.Parser;
import io.github.treesitter.jtreesitter.Tree;

/**
 * Base class for Tree-sitter parsers.
 * <p>
 * This abstract class provides the foundation for implementing language-specific Tree-sitter parsers. It handles the
 * common parsing workflow including file reading, Tree-sitter parsing, and token extraction. Subclasses must implement
 * the language-specific grammar loading and token extraction logic.
 * </p>
 * <p>
 * The class manages a Tree-sitter {@link Parser} instance and provides a unified interface for parsing multiple files
 * and extracting tokens.
 * </p>
 */
public abstract class AbstractTreeSitterParser {
    /** Tree-sitter parser instance for this language. */
    protected final Parser parser;

    /**
     * Creates a new Tree-sitter parser with the language grammar. Initializes the Tree-sitter parser with the
     * language grammar obtained from the subclass implementation.
     */
    protected AbstractTreeSitterParser() {
        Language language = new Language(getLanguageMemorySegment());
        this.parser = new Parser(language);
    }

    /**
     * Returns the memory segment containing the Tree-sitter language grammar.
     * <p>
     * Subclasses must implement this method to provide the native memory segment that contains the Tree-sitter language
     * grammar. This is typically obtained from a {@link TreeSitterLanguage} implementation.
     * </p>
     * @return The memory segment containing the language grammar
     */
    protected abstract MemorySegment getLanguageMemorySegment();

    /**
     * Parses multiple files and extracts tokens from each.
     * <p>
     * This method processes each file in the provided set, parsing it with Tree-sitter and extracting language-specific
     * tokens. The tokens are collected into a single list that represents all the parsed content for plagiarism detection.
     * </p>
     * @param files The set of files to parse
     * @return A list of tokens extracted from all files
     * @throws ParsingException If any file cannot be read or parsed
     */
    public List<Token> parse(Set<File> files) throws ParsingException {
        List<Token> tokens = new ArrayList<>();
        for (File file : files) {
            tokens.addAll(parseFile(file));
        }
        return tokens;
    }

    /**
     * Parses a single file and extracts tokens from its syntax tree.
     * <p>
     * Reads the file content, parses it with Tree-sitter to create an AST, and delegates token extraction to the subclass
     * implementation.
     * </p>
     * @param file The file to parse
     * @return A list of tokens extracted from the file's AST
     * @throws ParsingException If the file cannot be read or parsed
     */
    private List<Token> parseFile(File file) throws ParsingException {
        String code;

        try {
            code = Files.readString(file.toPath());
        } catch (IOException exception) {
            throw new ParsingException(file, "Failed to read file: " + exception.getMessage(), exception);
        }

        Tree tree = parser.parse(code, InputEncoding.UTF_8)
                .orElseThrow(() -> new ParsingException(file, "Tree-sitter failed to parse file: " + file.getName()));

        Node rootNode = tree.getRootNode();
        return extractTokens(file, rootNode);
    }

    /**
     * Extracts tokens from a Tree-sitter syntax tree.
     * <p>
     * Subclasses must implement this method to traverse the AST and extract language-specific tokens that are relevant for
     * plagiarism detection. The implementation should visit nodes in the tree and create appropriate {@link Token}
     * instances based on the node types and content.
     * </p>
     * @param file The source file being parsed
     * @param rootNode The root node of the syntax tree
     * @return A list of tokens extracted from the AST
     */
    protected abstract List<Token> extractTokens(File file, Node rootNode);
}
