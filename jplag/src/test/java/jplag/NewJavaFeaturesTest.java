package jplag;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NewJavaFeaturesTest extends TestBase {
    private static final String ROOT_DIRECTORY = "NewJavaFeatures";

    @Test
    public void testSimpleDuplicate() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions(ROOT_DIRECTORY);
        
        // Two submissions, one comparison with 100% match:
        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getComparisons().size());
        var comparison = result.getComparisons().get(0);
        assertEquals(100, comparison.percent(), Double.MIN_NORMAL);
        
        // Five matches that each start at the same position:
        assertEquals(5, comparison.matches.size());
        for (Match match : comparison.matches) {
            assertEquals(match.startA, match.startB);
        }
    }

}
