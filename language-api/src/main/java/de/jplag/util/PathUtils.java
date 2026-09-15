package de.jplag.util;

import java.nio.file.Path;

/**
 * Utility functions for {@link Path}
 */
public class PathUtils {
    /**
     * Returns the path with the same parent as the given path and the same filename, except that the suffix is appended.
     * For example: a/b/c.java, .bak -> a/b/c.java.bak
     *
     * @param path The path to modify
     * @param suffix The suffix to append
     * @return The modified path
     * @throws IllegalArgumentException If the given path is the root of it's filesystem
     */
    public static Path appendSuffix(Path path, String suffix) {
        if (path.getParent() == null) {
            throw new IllegalArgumentException("Cannot append a suffix to the root of a filesystem");
        } else {
            return path.getParent().resolve(path.getFileName().toString() + suffix);
        }
    }
}
