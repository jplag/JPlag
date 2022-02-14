package de.jplag;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.RootDirectoryException;

public class BaseCodeTest extends TestBase {

    @Test
    public void testBasecodeUserSubmissionComparison() throws ExitException {
        JPlagResult result = runJPlag("basecode", it -> it.setBaseCodeSubmissionName("base"));
        verifyResults(result);
    }

    @Test(expected = BasecodeException.class)
    public void testTinyBasecode() throws ExitException {
        runJPlag("TinyBasecode", it -> it.setBaseCodeSubmissionName("base"));
    }

    @Test
    public void testEmptySubmission() throws ExitException {
        JPlagResult result = runJPlag("emptysubmission", it -> it.setBaseCodeSubmissionName("base"));
        verifyResults(result);
    }

    @Test
    public void testAutoTrimFileSeparators() throws ExitException {
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
    public void testBasecodePathComparison() throws ExitException {
        JPlagResult result = runJPlag("basecode", it -> it.setBaseCodeSubmissionName(getBasePath("basecode-base")));
        assertEquals(3, result.getNumberOfSubmissions()); // "basecode/base" is now a user submission.
    }

    @Test(expected = RootDirectoryException.class)
    public void testInvalidRoot() throws ExitException {
        runJPlag("basecode", it -> it.setRootDirectoryNames(List.of("WrongRoot")));
    }

    @Test(expected = BasecodeException.class)
    public void testInvalidBasecode() throws ExitException {
        runJPlag("basecode", it -> it.setBaseCodeSubmissionName("WrongBasecode"));
    }

    @Test(expected = BasecodeException.class)
    public void testBasecodeUserSubmissionWithDots() throws ExitException {
        runJPlag("basecode", it -> it.setBaseCodeSubmissionName("base.ext"));
    }
}
