package de.jplag.reporting.reportobject.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This record represents a version number according to <a href="https://semver.org/">https://semver.org/</a>. This
 * version number is used to compare generated result.zip files with the version of the report-viewer.
 * @param major MAJOR version when you make incompatible API changes
 * @param minor MINOR version when you add functionality in a backwards compatible manner
 * @param patch PATCH version when you make backwards compatible bug fixes
 */
public record Version(@JsonProperty("major") int major, @JsonProperty("minor") int minor, @JsonProperty("patch") int patch) {

    private static final Logger logger = LoggerFactory.getLogger(Version.class);

    /**
     * The default version for development (0.0.0).
     */
    public static final Version DEVELOPMENT = new Version(0, 0, 0);

    /**
     * Parse a version string (e.g., {@code v0.0.1} or {@code 0.0.1} or {@code 0.0.1-SNAPSHOT}).
     * @param version the version string
     * @return the parsed version or {@code null} if not parsable
     */
    public static Version parseVersion(String version) {
        String plainVersion = version.startsWith("v") ? version.substring(1) : version;
        plainVersion = plainVersion.replace("-SNAPSHOT", "");

        if (!plainVersion.matches("\\d+\\.\\d+\\.\\d+")) {
            logger.debug("Version {} could not be parsed. Defaulting to null.", version);
            return null;
        }
        String[] versionParts = plainVersion.split("\\.");
        return new Version(Integer.parseInt(versionParts[0]), Integer.parseInt(versionParts[1]), Integer.parseInt(versionParts[2]));
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }
}
