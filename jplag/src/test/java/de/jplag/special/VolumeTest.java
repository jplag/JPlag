package de.jplag.special;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.jplag.TestBase;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

/**
 * These tests are not intended to be used automatically but rather manually prior to releases. These tests shall be
 * used with large data sets (e.g., 100+ submissions) in order to validate the core functionality.
 */
public class VolumeTest extends TestBase {

    /**
     * This path ensures that volume testing data is not in the git repository (with default configuration)
     */
    private static final String VOLUME_BASE_PATH = "../../volume";

    @Override
    protected String getBasePath() {
        return VOLUME_BASE_PATH;
    }

    /**
     * This test requires a folder "data" with submissions and a file named "matches_avg.csv" inside the volume folder.
     * Accepts a derivation of 0.1% in the matching percentage
     */
    @Test
    @Disabled
    public void volumeComparisonTest() throws ExitException, IOException {

        // Always succeed if not executed in an appropriate environment
        if (!new File(this.getBasePath()).exists()) {
            return;
        }

        var results = runJPlag("data", jPlagOptions -> jPlagOptions.setMaximumNumberOfComparisons(-1));

        var csv = readCSVResults(String.format("%s/%s", this.getBasePath(), "matches_avg.csv"));

        assertEquals(csv.size(), results.getComparisons().size());
        System.out.println("Volume test size: " + csv.size());

        results.getComparisons().forEach(result -> {
            var key = result.getFirstSubmission().getName() + result.getSecondSubmission().getName();

            assertTrue(csv.containsKey(key));
            assertEquals(csv.getOrDefault(key, -1f), result.similarity(), DELTA);
        });

    }

    private Map<String, Float> readCSVResults(String filePathAndName) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filePathAndName), JPlagOptions.CHARSET);
        var results = new HashMap<String, Float>();

        lines.forEach(line -> {
            var entries = line.split(";");

            if (entries.length != 4) {
                throw new IllegalArgumentException(String.format("Illegal line: '%s'", line));
            }

            results.put(entries[1] + entries[2], Float.parseFloat((entries[3])));
        });

        return results;
    }
}
