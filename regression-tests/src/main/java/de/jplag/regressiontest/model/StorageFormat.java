package de.jplag.regressiontest.model;

/**
 * Enum representing storage formats for datasets or files.
 */
public enum StorageFormat {
    /**
     * Storage as a directory in the file system.
     */
    DIRECTORY("data/%s"),
    /**
     * Storage as a compressed ZIP archive.
     */
    ZIP("data/%s.zip");

    private String defaultSource;

    StorageFormat(String defaultSource) {
        this.defaultSource = defaultSource;
    }

    /**
     * Returns the pattern for the default source path for the given storage type
     * @return The default source path pattern
     */
    public String getDefaultSource() {
        return defaultSource;
    }
}
