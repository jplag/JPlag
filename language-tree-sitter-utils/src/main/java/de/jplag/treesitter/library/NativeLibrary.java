package de.jplag.treesitter.library;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import de.jplag.treesitter.util.OS;

/**
 * Manages native library loading and caching for Tree-sitter language parsers.
 * <p>
 * This class handles the extraction, caching, and path resolution of native Tree-sitter libraries. It supports both
 * bundled libraries (extracted from JAR resources) and pre-installed libraries. Libraries are cached in the user's home
 * directory under {@code .jplag/libs/<version>/} to avoid repeated extraction and allow multiple versions to coexist.
 * </p>
 * <p>
 * The class automatically detects the current platform and loads the appropriate native library version for the
 * specified language.
 * </p>
 */
public final class NativeLibrary {
    private static final String HOME_DIR = "user.home";
    private static final Path NATIVE_LIBRARY_DIR = Path.of(System.getProperty(HOME_DIR), ".jplag", "libs");

    private final String version;
    private final String systemLibraryName;

    /**
     * Creates a new native library instance for the specified library.
     * @param name The base name of the native library
     * @param version The version of the library
     */
    public NativeLibrary(String name, String version) {
        this.version = version;
        this.systemLibraryName = System.mapLibraryName(name);
    }

    /**
     * Gets the resource path for the native library based on the current platform and version.
     * @return The URL pointing to the bundled native library resource
     * @throws IllegalStateException If the resource is not found for the current platform
     */
    private URL getResourcePath() {
        String path = String.format("/native/%s/%s/%s", OS.name(), version, systemLibraryName);
        URL url = NativeLibrary.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("Resource not found: " + path);
        }
        return url;
    }

    /**
     * Gets the path where the native library should be stored on disk.
     * @return The path in the user's home directory for caching the library
     */
    private Path getStoredLibraryPath() {
        return NATIVE_LIBRARY_DIR.resolve(version).resolve(systemLibraryName);
    }

    /**
     * Resolves the path to the native library, extracting it if necessary.
     * <p>
     * This method first checks if the library is already available as a file resource (for development). If not, it checks
     * if the library has been previously extracted to the cache directory. If neither exists, it extracts the library from
     * the bundled resources to the cache directory.
     * </p>
     * @return The path to the native library file
     * @throws RuntimeException If the library cannot be extracted or accessed
     */
    public Path getLibraryPath() {
        try {
            URL resourcePath = getResourcePath();
            Path storedLibraryPath = getStoredLibraryPath();

            if ("file".equals(resourcePath.getProtocol())) {
                return Path.of(resourcePath.toURI());
            } else if (Files.exists(storedLibraryPath)) {
                return storedLibraryPath;
            } else {
                Files.createDirectories(storedLibraryPath.getParent());
                try (InputStream in = resourcePath.openStream()) {
                    Files.copy(in, storedLibraryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                return storedLibraryPath;
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to resolve native library path", exception);
        }
    }
}
