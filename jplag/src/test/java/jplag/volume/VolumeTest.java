package jplag.volume;

import jplag.ExitException;
import jplag.JPlagResult;
import jplag.TestBase;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * These tests are not intended to be used automatically but rather manually prior to releases.
 * These tests shall be used with large data sets (e.g., 100+ submissions) in order to validate the core functionality.
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
     * This test requires a folder "data" with submissions and a file named "matches_avg.csv" inside the volume folder
     */
    @Test
    public void volumeComparisonTest() throws ExitException, IOException {

        // Always succeed if not executed in an appropriate environment
        if(!new File(this.getBasePath()).exists()) {
            return;
        }

        JPlagResult result = runJPlagWithOptions("data",
                jPlagOptions -> jPlagOptions.setMaxNumberOfMatches(-1));

        List<Result> csv = readCSVResults(String.format("%s/%s", this.getBasePath(), "matches_avg.csv"));

        assertEquals(csv.size(), result.getAllComparisons().size());
        System.out.println("Volume test size: " + csv.size());
        // TODO SH: More individual tests
    }

    private List<Result> readCSVResults(String filePathAndName) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filePathAndName));

        return lines.stream().map(line -> {
            var entries = line.split(";");

            if(entries.length != 4) {
                throw new IllegalArgumentException(String.format("Illegal line: '%s'", line));
            }

            return new Result(Integer.parseInt(entries[0]), entries[1], entries[2], Float.parseFloat((entries[3])));
        }).collect(Collectors.toList());
    }
}
