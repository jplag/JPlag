package de.jplag.treesitter.library;

import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;

import io.github.treesitter.jtreesitter.NativeLibraryLookup;

/**
 * Implementation of {@link NativeLibraryLookup} for Tree-sitter libraries.
 * <p>
 * This class provides the mechanism for looking up symbols in the Tree-sitter native library. It uses the
 * {@link NativeLibraryType#TREE_SITTER} library type to locate and load the appropriate native library for the current
 * platform.
 * </p>
 */
public class LibraryLookup implements NativeLibraryLookup {
    /**
     * Creates a symbol lookup for the Tree-sitter native library.
     * @param arena The memory arena for managing native library resources
     * @return A symbol lookup that can resolve Tree-sitter library symbols
     */
    @Override
    public SymbolLookup get(Arena arena) {
        return SymbolLookup.libraryLookup(NativeLibraryType.TREE_SITTER.create().getLibraryPath().toString(), arena);
    }
}
