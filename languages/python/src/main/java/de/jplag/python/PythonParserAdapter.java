package de.jplag.python;

import java.io.File;
import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.nio.file.Files;
import java.util.List;

import de.jplag.Token;
import de.jplag.treesitter.AbstractTreeSitterParserAdapter;
import de.jplag.treesitter.TreeSitterTraversal;

import io.github.treesitter.jtreesitter.Node;

public class PythonParserAdapter extends AbstractTreeSitterParserAdapter {
    @Override
    protected MemorySegment getLanguageMemorySegment() {
        return TreeSitterPython.language();
    }

    @Override
    protected List<Token> extractTokens(File file, Node rootNode) {
        try {
            // TODO: Consider caching file content to avoid re-reading
            String code = Files.readString(file.toPath());
            PythonTokenCollector collector = new PythonTokenCollector(file, code);
            TreeSitterTraversal.traverse(rootNode, collector);
            List<Token> tokens = collector.getTokens();
            tokens.add(Token.fileEnd(file));
            return tokens;
        } catch (IOException exception) {
            throw new RuntimeException("Failed to read file: " + file.getName(), exception);
        }
    }
}
