package de.jplag;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class BaseCodeTest extends TestBase {

    @Test
    public void testBasecodeComparison() throws ExitException {
        checkResult(runJPlag("basecode", it -> it.setBaseCodeSubmissionName("base")));
    }
    
    @Test
    public void testWeirdPath() throws ExitException {
        String weirdPath = "." + File.separator + ".." + File.separator + "basecode" + File.separator + "base";
        checkResult(runJPlag("basecode", it -> it.setBaseCodeSubmissionName(weirdPath)));
    }


    private void checkResult(JPlagResult result) {
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
    public void testInvalidBasecode2() throws ExitException {
        runJPlag("basecode", it -> it.setBaseCodeSubmissionName("." + File.separator + "base"));
    }

    @Test(expected = ExitException.class)
    public void testInvalidBasecode3() throws ExitException {
        runJPlag("basecode", it -> it.setBaseCodeSubmissionName(".." + File.separator + "base"));
    }

    @Test(expected = ExitException.class)
    public void testInvalidBasecode4() throws ExitException {
        runJPlag("basecode", it -> it.setBaseCodeSubmissionName(File.separator + "base" + File.separator));
    }
}
