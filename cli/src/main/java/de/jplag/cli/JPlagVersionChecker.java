package de.jplag.cli;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlag;
import de.jplag.reporting.reportobject.model.Version;

/**
 * Handles the check for newer versions.
 */
public class JPlagVersionChecker {
    private static final String API_URL = "https://api.github.com/repos/jplag/JPlag/releases";
    private static final Logger logger = LoggerFactory.getLogger(JPlagVersionChecker.class);
    private static final String EXPECTED_VERSION_FORMAT = "v\\d\\.\\d\\.\\d+";
    private static final String WARNING_UNABLE_TO_FETCH = "Unable to fetch version information. New version notification will not work.";
    private static final String NEWER_VERSION_AVAILABLE = "There is a newer version ({}) available. You can download the newest version here: https://github.com/jplag/JPlag/releases";
    private static final String UNEXPECTED_ERROR = "There was an unexpected error, when checking for new versions. Please report this on: https://github.com/jplag/JPlag/issues";

    private JPlagVersionChecker() {

    }

    /**
     * Prints a warning if a newer version is available on GitHub.
     */
    public static void printVersionNotification() {
        Optional<Version> newerVersion = checkForNewVersion();
        newerVersion.ifPresent(version -> logger.warn(NEWER_VERSION_AVAILABLE, version));
    }

    private static Optional<Version> checkForNewVersion() {
        try {
            JsonArray array = fetchApi();
            Version newest = getNewestVersion(array);
            Version current = JPlag.JPLAG_VERSION;

            if (newest.compareTo(current) > 0) {
                return Optional.of(newest);
            }
        } catch (IOException | URISyntaxException e) {
            logger.info(WARNING_UNABLE_TO_FETCH);
        } catch (Exception e) {
            logger.warn(UNEXPECTED_ERROR, e);
        }

        return Optional.empty();
    }

    private static JsonArray fetchApi() throws IOException, URISyntaxException {
        URL url = new URI(API_URL).toURL();
        URLConnection connection = url.openConnection();

        try (JsonReader reader = Json.createReader(connection.getInputStream())) {
            return reader.readArray();
        }
    }

    private static Version getNewestVersion(JsonArray apiResult) {
        return apiResult.stream().map(JsonObject.class::cast).map(version -> version.getString("name"))
                .filter(versionName -> versionName.matches(EXPECTED_VERSION_FORMAT)).limit(1).map(JPlagVersionChecker::parseVersion).findFirst()
                .orElse(JPlag.JPLAG_VERSION);
    }

    /**
     * Parses the version name.
     * @param versionName The version name. The expected format is: v[major].[minor].[patch]
     * @return The parsed version
     */
    private static Version parseVersion(String versionName) {
        String withoutPrefix = versionName.substring(1);
        String[] parts = withoutPrefix.split("\\.");
        return parseVersionParts(parts);
    }

    private static Version parseVersionParts(String[] parts) {
        return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }
}
