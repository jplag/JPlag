package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.Test;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;

/**
 * Tests for the legacy behaviour of the String-based base code initializer.
 */
@Deprecated(since = "4.0.0", forRemoval = true)
class LegacyBaseCodeTest extends BaseCodeTest {
    @Test
    void testBasecodeUserSubmissionComparison() throws ExitException {
        JPlagResult result = runJPlag("basecode", it -> it.withBaseCodeSubmissionName("base"));
        verifyResults(result);
    }

    @Test
    void testTinyBasecode() {
        assertThrows(BasecodeException.class, () -> runJPlag("TinyBasecode", it -> it.withBaseCodeSubmissionName("base")));
    }

    @Test
    void testEmptySubmission() throws ExitException {
        JPlagResult result = runJPlag("emptysubmission", it -> it.withBaseCodeSubmissionName("base"));
        verifyResults(result);
    }

    @Test
    void testAutoTrimFileSeparators() throws ExitException {
        JPlagResult result = runJPlag("basecode", it -> it.withBaseCodeSubmissionName(File.separator + "base" + File.separator));
        verifyResults(result);
    }

    @Test
    void testBasecodePathComparison() throws ExitException {
        JPlagResult result = runJPlag("basecode", it -> it.withBaseCodeSubmissionName(getBasePath("basecode-base")));
        assertEquals(3, result.getNumberOfSubmissions()); // "basecode/base" is now a user submission.
    }

    @Test
    void testInvalidBasecode() {
        assertThrows(BasecodeException.class, () -> runJPlag("basecode", it -> it.withBaseCodeSubmissionName("WrongBasecode")));
    }

    @Test
    void testBasecodeUserSubmissionWithDots() {
        assertThrows(IllegalArgumentException.class, () -> runJPlag("basecode", it -> it.withBaseCodeSubmissionName("base.ext")));
    }

    /**
     * The simple duplicate contains obvious plagiarism.
     */
    @Test
    void testSubdirectoryGlobalBasecode() throws ExitException {
        String basecode = getBasePath("SubdirectoryBase");
        JPlagResult result = runJPlag("SubdirectoryDuplicate", it -> it.withSubdirectoryName("src").withBaseCodeSubmissionName(basecode));
        verifySimpleSubdirectoryDuplicate(result, 3, 3);
    }

    /**
     * The simple duplicate contains obvious plagiarism.
     */
    @Test
    void testSubdirectoryLocalBasecode() throws ExitException {
        JPlagResult result = runJPlag("SubdirectoryDuplicate", it -> it.withSubdirectoryName("src").withBaseCodeSubmissionName("Base"));
        verifySimpleSubdirectoryDuplicate(result, 2, 1);
    }
}
