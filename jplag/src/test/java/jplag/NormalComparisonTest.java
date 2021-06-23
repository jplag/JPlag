package jplag;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NormalComparisonTest extends TestBase {

    @Test
    public void testSimpleDuplicate() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("SimpleDuplicate");

        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getComparisons().size());
        assertEquals(1, result.getComparisons().get(0).matches.size());
        assertEquals(1, result.getSimilarityDistribution()[6]);
    }

}
