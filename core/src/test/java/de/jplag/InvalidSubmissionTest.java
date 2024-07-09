package de.jplag;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.SubmissionException;

class InvalidSubmissionTest extends TestBase {

    private static final String SAMPLE_NAME = "InvalidSubmissions";

    @Test
    @DisplayName("test that it works with a single submission")
    void testSingleSubmission() throws ExitException {
        List<String> submissions = new ArrayList<>();
        submissions.add(getBasePath("basecode/A"));
        submissions.add(getBasePath("basecode/B"));
        submissions.add(getBasePath("basecode/base"));
        JPlagResult result = runJPlag(submissions, it -> it);
        List<String> results = result.getSubmissions().getSubmissions().stream().map(Submission::getName).sorted().toList();
        assertEquals(results.get(0), "A"+File.separator+"TerrainType.java");
        assertEquals(results.get(1), "B"+File.separator+"TerrainType.java");
        assertEquals(results.get(2), "base"+File.separator+"TerrainType.java");

    }

    /**
     * Tests if invalid submissions are correctly filtered, leading to no valid submissions. The debug options lead to the
     * invalid submissions being stored.
     */
    @Test
    @DisplayName("test filtering and copying of invalid submissions with debug mode")
    void testInvalidSubmissionsWithDebug() throws ExitException {
        try {
            runJPlag(SAMPLE_NAME, it -> it.withDebugParser(true));
            fail("No submission exception was thrown!");
        } catch (SubmissionException e) {
            System.out.println(e.getMessage());
        } finally {
            File errorFolder = new File(Path.of("errors", "java").toString());
            assertTrue(errorFolder.exists());
            String[] errorSubmissions = errorFolder.list();
            if (errorSubmissions != null)
                Arrays.sort(errorSubmissions); // File systems don't promise alphabetical order.
            deleteDirectory(errorFolder.getParentFile());
            assertArrayEquals(new String[]{"A", "B"}, errorSubmissions);
        }
    }
}
