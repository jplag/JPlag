package de.jplag.python;

import java.io.File;
import java.lang.foreign.MemorySegment;
import java.util.List;

import de.jplag.Token;
import de.jplag.treesitter.AbstractTreeSitterParserAdapter;

import io.github.treesitter.jtreesitter.Node;

public class PythonParserAdapter extends AbstractTreeSitterParserAdapter {

    @Override
    protected MemorySegment getLanguageMemorySegment() {
        return TreeSitterPython.language();
    }

    @Override
    protected List<Token> extractTokens(File file, Node rootNode) {
        throw new UnsupportedOperationException("Unimplemented method 'extractTokens'");
    }
}
