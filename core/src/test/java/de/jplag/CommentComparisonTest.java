package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.exceptions.ExitException;

/**
 * Tests the comment extraction & comparison functionality, which extracts and compares the comments from the source
 * files and compares them separately from the code.
 */
public class CommentComparisonTest extends TestBase {

    @Test
    @DisplayName("test comment comparison on two submissions")
    void testCommentsTwoSubmissions() throws ExitException {
        JPlagResult result = runJPlag("comments", it -> it.withAnalyzeComments(true));

        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getAllComparisons().size());
        assertEquals(1, result.getAllComparisons().get(0).matches().size());
        assertEquals(2, result.getAllComparisons().get(0).commentMatches().size());
        assertEquals(0.8406, result.getAllComparisons().get(0).similarity(), DELTA);
    }

    @Test
    @DisplayName("test comment comparison on two submissions with basecode")
    void testCommentsTwoSubmissionsAndBaseCode() throws ExitException {
        JPlagResult result = runJPlag("comments-basecode",
                it -> it.withAnalyzeComments(true).withBaseCodeSubmissionDirectory(new File(it.submissionDirectories().iterator().next(), "base")));

        assertEquals(2, result.getNumberOfSubmissions());
        assertEquals(1, result.getAllComparisons().size());
        assertEquals(0, result.getAllComparisons().get(0).matches().size());
        assertEquals(2, result.getAllComparisons().get(0).commentMatches().size());
        assertEquals(0.5778, result.getAllComparisons().get(0).similarity(), DELTA);

        assertEquals(0, result.getSubmissions().getSubmissions().get(0).getBaseCodeComparison().matches().size());
        assertEquals(1, result.getSubmissions().getSubmissions().get(0).getBaseCodeComparison().commentMatches().size());
        assertEquals(0, result.getSubmissions().getSubmissions().get(1).getBaseCodeComparison().matches().size());
        assertEquals(1, result.getSubmissions().getSubmissions().get(1).getBaseCodeComparison().commentMatches().size());
    }

    @Test
    @DisplayName("test that comment comparison never reduces similarity")
    void testCommentsNeverReduceSimilarity() throws ExitException {
        JPlagResult withoutComments = runJPlagWithDefaultOptions("comments");
        JPlagResult withComments = runJPlag("comments", it -> it.withAnalyzeComments(true));

        assertEquals(2, withoutComments.getNumberOfSubmissions());
        assertEquals(2, withComments.getNumberOfSubmissions());
        assertEquals(1, withoutComments.getAllComparisons().size());
        assertEquals(1, withComments.getAllComparisons().size());
        double similarityWithoutComments = withoutComments.getAllComparisons().get(0).similarity();
        double similarityWithComments = withComments.getAllComparisons().get(0).similarity();
        assertFalse(similarityWithComments < similarityWithoutComments);
    }

}
