package de.jplag.highlight_extraction.frequencyDetermination;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LabelledWeightingTest {

    Submission s1;
    Submission s2;
    Submission s3;

    JPlagComparison c1;
    JPlagComparison c2;
    JPlagComparison c3;

    @BeforeEach
    void setup() {
        s1 = mock(Submission.class);
        when(s1.getName()).thenReturn("s1");
        s2 = mock(Submission.class);
        when(s2.getName()).thenReturn("s2");
        s3 = mock(Submission.class);
        when(s3.getName()).thenReturn("s3");

        c1 = mock(JPlagComparison.class);
        when(c1.firstSubmission()).thenReturn(s1);
        when(c1.secondSubmission()).thenReturn(s2);
        when(c1.similarity()).thenReturn(0.75);

        c2 = mock(JPlagComparison.class);
        when(c2.firstSubmission()).thenReturn(s2);
        when(c2.secondSubmission()).thenReturn(s3);
        when(c2.similarity()).thenReturn(0.6);

        c3 = mock(JPlagComparison.class);
        when(c3.firstSubmission()).thenReturn(s1);
        when(c3.secondSubmission()).thenReturn(s3);
        when(c3.similarity()).thenReturn(0.4);
    }

    @Test
    void testClassificationAndGrouping() {
        List<JPlagComparison> comparisons = List.of(c1, c2, c3);

        LabelledWeighting lw = new LabelledWeighting();
        lw.classifyComparisons0(comparisons, null);

        assertTrue(lw.getPlagiatComparisons().contains(c1));

        assertFalse(lw.getAuffaelligComparisons().contains(c2));
        assertFalse(lw.getPlagiatComparisons().contains(c2));
        assertFalse(lw.getUnauffaelligComparisons().contains(c2));
        assertFalse(lw.getUnauffaelligComparisons().contains(c3));

        assertEquals(1, lw.getPlagiatComparisons().size());
        assertEquals(0, lw.getAuffaelligComparisons().size());
        assertEquals(0, lw.getUnauffaelligComparisons().size());
    }
}
