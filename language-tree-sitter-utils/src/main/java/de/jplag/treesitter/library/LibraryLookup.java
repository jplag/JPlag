package de.jplag.treesitter.library;

import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;

import io.github.treesitter.jtreesitter.NativeLibraryLookup;

public class LibraryLookup implements NativeLibraryLookup {
    @Override
    public SymbolLookup get(Arena arena) {
        return SymbolLookup.libraryLookup(NativeLibraryType.TREE_SITTER.create().getLibraryPath().toString(), arena);
    }
}
