package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This record represents a version number according to <a href="https://semver.org/">https://semver.org/</a>.
 * This version number is used to compare generated result.zip files with the version of the report-viewer.
 *
 * @param major MAJOR version when you make incompatible API changes
 * @param minor MINOR version when you add functionality in a backwards compatible manner
 * @param patch PATCH version when you make backwards compatible bug fixes
 */
public record Version(@JsonProperty("major") int major, @JsonProperty("minor") int minor,
                      @JsonProperty("patch") int patch) {
}
