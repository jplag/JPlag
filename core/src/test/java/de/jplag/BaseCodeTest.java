package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.RootDirectoryException;

/**
 * Tests the basecode functionality, that allows specifying a shared foundation, from which all submissions were
 * derived. The parts of the submissions that match with the basecode are ignored for the comparison. The basecode
 * feature is tested in combination with different root folders and the subdirectory feature.
 */
public class BaseCodeTest extends TestBase {

    @Test
    @DisplayName("test two submissions with basecode in root folder")
    void testBasecodeUserSubmissionComparison() throws ExitException {
        JPlagResult result = runJPlag("basecode",
                it -> it.withBaseCodeSubmissionDirectory(new File(it.submissionDirectories().iterator().next(), "base")));
        verifyResults(result);
    }

    @Test
    @DisplayName("test basecode that is too small")
    void testTinyBasecode() {
        assertThrows(BasecodeException.class, () -> runJPlag("TinyBasecode",
                it -> it.withBaseCodeSubmissionDirectory(new File(it.submissionDirectories().iterator().next(), "base"))));
    }

    @Test
    @DisplayName("test empty submissions with basecode")
    void testEmptySubmission() throws ExitException {
        JPlagResult result = runJPlag("emptysubmission",
                it -> it.withBaseCodeSubmissionDirectory(new File(it.submissionDirectories().iterator().next(), "base")));
        verifyResults(result);
    }

    protected void verifyResults(JPlagResult result) {
        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getAllComparisons().size());
        assertEquals(1, result.getAllComparisons().get(0).matches().size());
        assertEquals(1, result.getSimilarityDistribution()[8]);
        assertEquals(0.8125, result.getAllComparisons().get(0).similarity(), DELTA);
    }

    @Test
    @DisplayName("test external basecode that is not in a root folder")
    void testBasecodePathComparison() throws ExitException {
        JPlagResult result = runJPlag("basecode", it -> it.withBaseCodeSubmissionDirectory(new File(BASE_PATH, "basecode-base")));
        assertEquals(3, result.getNumberOfSubmissions()); // "basecode/base" is now a user submission.
    }

    @Test
    @DisplayName("test invalid root folder")
    void testInvalidRoot() {
        assertThrows(RootDirectoryException.class, () -> runJPlagWithDefaultOptions("WrongRoot"));
    }

    @Test
    @DisplayName("test invalid basecode folder")
    void testInvalidBasecode() {
        assertThrows(BasecodeException.class, () -> runJPlag("basecode",
                it -> it.withBaseCodeSubmissionDirectory(new File(it.submissionDirectories().iterator().next(), "WrongBasecode"))));
    }

    @Test
    @DisplayName("test external basecode with subdirectory")
    void testSubdirectoryGlobalBasecode() throws ExitException {
        String basecode = getBasePath("SubdirectoryBase");
        JPlagResult result = runJPlag("SubdirectoryDuplicate",
                it -> it.withSubdirectoryName("src").withBaseCodeSubmissionDirectory(new File(basecode)));
        verifySimpleSubdirectoryDuplicate(result, 3, 3);
    }

    @Test
    @DisplayName("test basecode in root folder with subdirectory")
    void testSubdirectoryLocalBasecode() throws ExitException {
        JPlagResult result = runJPlag("SubdirectoryDuplicate",
                it -> it.withSubdirectoryName("src").withBaseCodeSubmissionDirectory(new File(it.submissionDirectories().iterator().next(), "Base")));
        verifySimpleSubdirectoryDuplicate(result, 2, 1);
    }

    protected void verifySimpleSubdirectoryDuplicate(JPlagResult result, int submissions, int comparisons) {
        result.getSubmissions().getSubmissions().forEach(this::hasSubdirectoryRoot);
        hasSubdirectoryRoot(result.getSubmissions().getBaseCode());

        assertEquals(submissions, result.getNumberOfSubmissions());
        assertEquals(comparisons, result.getAllComparisons().size());
        assertEquals(1, result.getAllComparisons().get(0).matches().size());
        assertEquals(1, result.getSimilarityDistribution()[9]);
        assertEquals(0.9473, result.getAllComparisons().get(0).similarity(), DELTA);
    }

    private void hasSubdirectoryRoot(Submission submission) {
        assertEquals("src", submission.getRoot().getName());
    }
}
