package de.jplag.python;

import java.lang.foreign.MemorySegment;

import de.jplag.treesitter.TreeSitterLanguage;
import de.jplag.treesitter.library.NativeLibraryType;

public class TreeSitterPython extends TreeSitterLanguage {

    private static final String PYTHON_SYMBOL_NAME = "tree_sitter_python";
    private static final TreeSitterPython INSTANCE = new TreeSitterPython();

    private TreeSitterPython() {
    }

    public static MemorySegment language() {
        return INSTANCE.call();
    }

    @Override
    protected NativeLibraryType libraryType() {
        return NativeLibraryType.TREE_SITTER_PYTHON;
    }

    @Override
    protected String symbolName() {
        return PYTHON_SYMBOL_NAME;
    }
}
