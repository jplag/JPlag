package de.jplag;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.SubmissionException;

class InvalidSubmissionTest extends TestBase {

    private static final String SAMPLE_NAME = "InvalidSubmissions";

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
            assertArrayEquals(new String[] {"A", "B"}, errorSubmissions);
        }
    }
}
