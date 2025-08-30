package de.jplag.treesitter.library;

/**
 * Enumeration of supported Tree-sitter native library types.
 * <p>
 * This enum defines the available Tree-sitter language libraries and their versions. Each library type represents a
 * specific language grammar that can be used for parsing source code with Tree-sitter.
 * </p>
 */
public enum NativeLibraryType {
    /**
     * The core Tree-sitter library containing the parsing engine
     */
    TREE_SITTER("tree-sitter", "0.25.8"),
    /**
     * Python language grammar for Tree-sitter
     */
    TREE_SITTER_PYTHON("tree-sitter-python", "0.23.6");

    private final String name;
    private final String version;

    /**
     * Creates a new native library type with the specified name and version.
     * @param name The base name of the native library
     * @param version The version of the library
     */
    NativeLibraryType(String name, String version) {
        this.name = name;
        this.version = version;
    }

    /**
     * Creates a new {@link NativeLibrary} instance for this library type.
     * @return A new NativeLibrary configured with this type's name and version
     */
    public NativeLibrary create() {
        return new NativeLibrary(name, version);
    }
}
