package de.jplag.util;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * Represents a sequence of names, similar to {@link Path}, but is independent of an underlying file system.
 * <p>
 * Use this to represent relative paths independent of the file system. This class does not handle any separators ('/') or special files ('.', '..'). If these are used the behavior will change depending on the file system this is applied to.
 */
public class RelativePath {
    private static final String TO_STRING_SEPARATOR = "/";
    private String[] segments;

    /**
     * Builds a new {@link RelativePath} with the given names
     *
     * @param names The names of the path
     */
    public RelativePath(String... names) {
        this.segments = names;
    }

    /**
     * Builds a new {@link RelativePath} with the given names
     *
     * @param names The names of the path
     * @return The constructed relative path
     */
    public static RelativePath of(String... names) {
        return new RelativePath(names);
    }

    /**
     * Builds a new relative path containing all names of the actual path.
     * This can be used to transfer a relative path from one file system to another.
     *
     * @param realPath The path to convert
     * @return The {@link RelativePath}
     */
    public static RelativePath of(Path realPath) {
        String[] segments = new String[realPath.getNameCount()];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = realPath.getName(i).toString();
        }
        return new RelativePath(segments);
    }

    /**
     * Creates a new path that contains the names of this path first and then all the given names. Similar to {@link Path#resolve}.
     *
     * @param parts The names to append
     * @return The new path
     */
    public RelativePath resolve(String... parts) {
        return resolve(new RelativePath(parts));
    }

    /**
     * Creates a new path that concatenates this path and the given one.
     *
     * @param other The path to concatenate with
     * @return The concatenated path
     */
    public RelativePath resolve(RelativePath other) {
        String[] target = new String[segments.length + other.segments.length];
        System.arraycopy(segments, 0, target, 0, segments.length);
        System.arraycopy(other.segments, 0, target, segments.length, other.segments.length);
        return new RelativePath(target);
    }

    /**
     * Resolves this path to the given actual path.
     * @param base The path to resolve against
     * @return The actual path that is created by resolving the names of this relative path on the actual path
     */
    public Path resolveAgainst(Path base) {
        if (segments.length == 0) {
            return base;
        }
        String first = segments[0];
        String[] others = Arrays.copyOfRange(segments, 1, segments.length);
        return base.resolve(first, others);
    }

    /**
     * @return The number of names in this relative path
     */
    public int getNameCount() {
        return this.segments.length;
    }

    /**
     * @param i The index of the name to get
     * @return The name at index i
     */
    public String getName(int i) {
        return segments[i];
    }

    @Override
    public String toString() {
        return String.join(TO_STRING_SEPARATOR, segments);
    }
}
