package de.jplag;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class BaseCodeTest extends TestBase {

    @Test
    public void testBasecodeComparison() throws ExitException {
        JPlagResult result = runJPlag("basecode", it -> it.setBaseCodeSubmissionName("base"));
        verifyResults(result);
    }
    
    @Test(expected = ExitException.class)
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

    @Test(expected = ExitException.class)
    public void testInvalidRoot() throws ExitException {
        runJPlag("basecode", it -> it.setRootDirectoryName("WrongRoot"));
    }

    @Test(expected = ExitException.class)
    public void testInvalidBasecode() throws ExitException {
        runJPlag("basecode", it -> it.setBaseCodeSubmissionName("WrongBasecode"));
    }

    @Test(expected = ExitException.class)
    public void testBasecodeWithDots() throws ExitException {
        runJPlag("basecode", it -> it.setBaseCodeSubmissionName("." + File.separator + "base"));
    }
}
