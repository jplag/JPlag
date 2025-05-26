package de.jplag.treesitter.library;

public enum NativeLibraryType {
    TREE_SITTER("tree-sitter", "0.25.1"),
    TREE_SITTER_JAVA("tree-sitter-java", "0.23.5"),
    TREE_SITTER_PYTHON("tree-sitter-python", "0.23.6");

    private final String name;
    private final String version;

    NativeLibraryType(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public NativeLibrary create() {
        return new NativeLibrary(name, version);
    }
}
