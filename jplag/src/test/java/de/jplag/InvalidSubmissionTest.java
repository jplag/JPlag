package de.jplag;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Test;

import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.SubmissionException;

public class InvalidSubmissionTest extends TestBase {

    private static final String SAMPLE_NAME = "InvalidSubmissions";

    /**
     * Tests if invalid submissions are correctly filtered, leading to no valid submissions. The debug options lead to the
     * invalid submissions being stored.
     */
    @Test
    public void testInvalidSubmissionsWithDebug() throws ExitException {
        try {
            runJPlag(SAMPLE_NAME, it -> it.setDebugParser(true));
            fail("No submission exception was thrown!");
        } catch (SubmissionException e) {
            System.out.println(e.getMessage());
        } finally {
            File errorFolder = new File(Path.of(BASE_PATH, SAMPLE_NAME, "errors", "java").toString());
            assertTrue(errorFolder.exists());
            String[] errorSubmissions = errorFolder.list();
            Arrays.sort(errorSubmissions); // File systems don't promise alphabetical order.
            deleteDirectory(errorFolder.getParentFile());
            assertArrayEquals(new String[] {"A", "B"}, errorSubmissions);
        }
    }

    private static void deleteDirectory(File path) {
        if (path.exists()) {
            for (File file : path.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        path.delete();
    }

}
