package de.jplag.treesitter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;

import io.github.treesitter.jtreesitter.InputEncoding;
import io.github.treesitter.jtreesitter.Language;
import io.github.treesitter.jtreesitter.Node;
import io.github.treesitter.jtreesitter.Parser;
import io.github.treesitter.jtreesitter.Tree;

/**
 * Base class for Tree-sitter parser adapters.
 */
public abstract class AbstractTreeSitterParserAdapter extends AbstractParser {
    protected final Parser parser;

    /**
     * Creates a new {@code AbstractTreeSitterParserAdapter} for the specified {@link TSLanguage}.
     * @param language The {@link Language} instance representing the grammar and parsing rules to be used by this adapter
     */
    public AbstractTreeSitterParserAdapter(Language language) {
        this.parser = new Parser(language);
    }

    /**
     * Parses the given set of files and extracts tokens from each.
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
     * Traverses the syntax tree (AST) for the given file and collects all relevant tokens.
     * @param file The file being parsed
     * @param rootNode The root node of the syntax tree
     * @return A list of tokens extracted from the AST of the file
     */
    protected abstract List<Token> extractTokens(File file, Node rootNode);
}
