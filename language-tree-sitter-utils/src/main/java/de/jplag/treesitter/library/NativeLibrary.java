package de.jplag.treesitter.library;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import de.jplag.treesitter.util.OS;

public final class NativeLibrary {
    private static final String HOME_DIR = "user.home";
    private static final Path NATIVE_LIBRARY_DIR = Path.of(System.getProperty(HOME_DIR), ".jplag", "libs");

    private final String name;
    private final String version;
    private final String systemLibraryName;

    public NativeLibrary(String name, String version) {
        this.name = name;
        this.version = version;
        this.systemLibraryName = System.mapLibraryName(name);
    }

    private URL getResourcePath() {
        String path = String.format("/native/%s/%s", OS.name(), systemLibraryName);
        URL url = NativeLibrary.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("Resource not found: " + path);
        }
        return url;
    }

    private Path getStoredLibraryPath() {
        return NATIVE_LIBRARY_DIR.resolve(systemLibraryName);
    }

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
