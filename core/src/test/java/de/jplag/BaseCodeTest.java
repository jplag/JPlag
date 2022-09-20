package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.RootDirectoryException;

public class BaseCodeTest extends TestBase {

    @Test
    void testBasecodeUserSubmissionComparison() throws ExitException {
        JPlagResult result = runJPlag("basecode",
                it -> it.withBaseCodeSubmissionDirectory(new File(it.submissionDirectories().iterator().next(), "base")));
        verifyResults(result);
    }

    @Test
    void testTinyBasecode() {
        assertThrows(BasecodeException.class, () -> runJPlag("TinyBasecode",
                it -> it.withBaseCodeSubmissionDirectory(new File(it.submissionDirectories().iterator().next(), "base"))));
    }

    @Test
    void testEmptySubmission() throws ExitException {
        JPlagResult result = runJPlag("emptysubmission",
                it -> it.withBaseCodeSubmissionDirectory(new File(it.submissionDirectories().iterator().next(), "base")));
        verifyResults(result);
    }

    protected void verifyResults(JPlagResult result) {
        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getAllComparisons().size());
        assertEquals(1, result.getAllComparisons().get(0).matches().size());
        assertEquals(1, result.getSimilarityDistribution()[1]);
        assertEquals(0.85, result.getAllComparisons().get(0).similarity(), DELTA);
    }

    @Test
    void testBasecodePathComparison() throws ExitException {
        JPlagResult result = runJPlag("basecode", it -> it.withBaseCodeSubmissionDirectory(new File(BASE_PATH, "basecode-base")));
        assertEquals(3, result.getNumberOfSubmissions()); // "basecode/base" is now a user submission.
    }

    @Test
    void testInvalidRoot() {
        assertThrows(RootDirectoryException.class, () -> runJPlag("basecode", it -> it.withSubmissionDirectories(Set.of(new File("WrongRoot")))));
    }

    @Test
    void testInvalidBasecode() {
        assertThrows(BasecodeException.class, () -> runJPlag("basecode",
                it -> it.withBaseCodeSubmissionDirectory(new File(it.submissionDirectories().iterator().next(), "WrongBasecode"))));
    }

    /**
     * The simple duplicate contains obvious plagiarism.
     */
    @Test
    void testSubdirectoryGlobalBasecode() throws ExitException {
        String basecode = getBasePath("SubdirectoryBase");
        JPlagResult result = runJPlag("SubdirectoryDuplicate",
                it -> it.withSubdirectoryName("src").withBaseCodeSubmissionDirectory(new File(basecode)));
        verifySimpleSubdirectoryDuplicate(result, 3, 3);
    }

    /**
     * The simple duplicate contains obvious plagiarism.
     */
    @Test
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
        assertEquals(1, result.getSimilarityDistribution()[3]);
        assertEquals(0.6207, result.getAllComparisons().get(0).similarity(), DELTA);
    }

    private void hasSubdirectoryRoot(Submission submission) {
        assertEquals("src", submission.getRoot().getName());
    }
}
