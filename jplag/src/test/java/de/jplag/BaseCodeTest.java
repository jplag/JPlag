package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.RootDirectoryException;

public class BaseCodeTest extends TestBase {

    @Test
    void testBasecodeUserSubmissionComparison() throws ExitException {
        JPlagResult result = runJPlag("basecode", it -> it.setBaseCodeSubmissionName("base"));
        verifyResults(result);
    }

    @Test
    void testTinyBasecode() {
        assertThrows(BasecodeException.class, () -> runJPlag("TinyBasecode", it -> it.setBaseCodeSubmissionName("base")));
    }

    @Test
    void testEmptySubmission() throws ExitException {
        JPlagResult result = runJPlag("emptysubmission", it -> it.setBaseCodeSubmissionName("base"));
        verifyResults(result);
    }

    @Test
    void testAutoTrimFileSeparators() throws ExitException {
        JPlagResult result = runJPlag("basecode", it -> it.setBaseCodeSubmissionName(File.separator + "base" + File.separator));
        verifyResults(result);
    }

    private void verifyResults(JPlagResult result) {
        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getComparisons().size());
        assertEquals(1, result.getComparisons().get(0).getMatches().size());
        assertEquals(1, result.getSimilarityDistribution()[8]);
        assertEquals(85f, result.getComparisons().get(0).similarity(), DELTA);
    }

    @Test
    void testBasecodePathComparison() throws ExitException {
        JPlagResult result = runJPlag("basecode", it -> it.setBaseCodeSubmissionName(getBasePath("basecode-base")));
        assertEquals(3, result.getNumberOfSubmissions()); // "basecode/base" is now a user submission.
    }

    @Test
    void testInvalidRoot() throws ExitException {
        assertThrows(RootDirectoryException.class, () -> runJPlag("basecode", it -> it.setSubmissionDirectories(List.of("WrongRoot"))));
    }

    @Test
    void testInvalidBasecode() {
        assertThrows(BasecodeException.class, () -> runJPlag("basecode", it -> it.setBaseCodeSubmissionName("WrongBasecode")));
    }

    @Test
    void testBasecodeUserSubmissionWithDots() {
        assertThrows(BasecodeException.class, () -> runJPlag("basecode", it -> it.setBaseCodeSubmissionName("base.ext")));
    }
}
