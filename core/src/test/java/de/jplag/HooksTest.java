package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import de.jplag.exceptions.ExitException;

class HooksTest extends TestBase {
    private static final String ROOT = "NoDuplicate";
    private static final int SUBMISSION_COUNT = 3;
    private static final int COMPARISON_COUNT = 3;

    @Test
    void testPreParseHook() throws ExitException {
        final AtomicInteger timesCalled = new AtomicInteger(0);
        runJPlag(ROOT, it -> it.withPreParseHook(submissions -> {
            timesCalled.incrementAndGet();
            assertEquals(SUBMISSION_COUNT, submissions.size());
        }));
        assertEquals(1, timesCalled.get());
    }

    @Test
    void testParseHook() throws ExitException {
        final AtomicInteger totalSubmissions = new AtomicInteger(0);
        runJPlag(ROOT, it -> it.withParseHook(submission -> {
            totalSubmissions.incrementAndGet();
        }));
        assertEquals(SUBMISSION_COUNT, totalSubmissions.get());
    }

    @Test
    void testPreCompareHook() throws ExitException {
        final AtomicInteger timesCalled = new AtomicInteger(0);
        runJPlag(ROOT, it -> it.withPreCompareHook(comparisons -> {
            timesCalled.incrementAndGet();
            assertEquals(COMPARISON_COUNT, comparisons.size());
        }));
        assertEquals(1, timesCalled.get());
    }

    @Test
    void testCompareHook() throws ExitException {
        final AtomicInteger totalComparisons = new AtomicInteger(0);
        runJPlag(ROOT, it -> it.withCompareHook(comparison -> {
            totalComparisons.incrementAndGet();
        }));
        assertEquals(COMPARISON_COUNT, totalComparisons.get());
    }
}
