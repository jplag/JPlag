package de.jplag.python;

import java.io.File;
import java.lang.foreign.MemorySegment;
import java.util.List;

import de.jplag.Token;
import de.jplag.treesitter.AbstractTreeSitterParser;

import io.github.treesitter.jtreesitter.Node;

public class PythonParser extends AbstractTreeSitterParser {
    @Override
    protected MemorySegment getLanguageMemorySegment() {
        return TreeSitterPython.language();
    }

    @Override
    protected List<Token> extractTokens(File file, Node rootNode) {
        PythonTokenCollector collector = new PythonTokenCollector(file);
        collector.traverse(rootNode);
        List<Token> tokens = collector.getTokens();
        tokens.add(Token.fileEnd(file));
        return tokens;
    }
}
