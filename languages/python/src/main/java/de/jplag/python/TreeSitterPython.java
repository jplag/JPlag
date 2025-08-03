package de.jplag.python;

import java.lang.foreign.MemorySegment;

import de.jplag.treesitter.TreeSitterLanguage;
import de.jplag.treesitter.library.NativeLibraryType;

public class TreeSitterPython extends TreeSitterLanguage {
    /**
     * Each Tree-sitter language implementation must use the singleton pattern to ensure proper native library management
     * and prevent memory leaks.
     */
    private static final TreeSitterPython INSTANCE = new TreeSitterPython();
    /**
     * The symbol name must follow the pattern {@code tree_sitter_<language>} as required by Tree-sitter.
     */
    private static final String SYMBOL_NAME = "tree_sitter_python";

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
        return SYMBOL_NAME;
    }
}
