package de.jplag.reporting.jsonfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.Token;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.FilePathUtil;
import de.jplag.reporting.reportobject.model.BaseCodeMatch;
import de.jplag.reporting.reportobject.model.CodePosition;
import de.jplag.reporting.reportobject.model.ComparisonReport;

class ReportTokenPositionTestTest {

    @Test
    void testCorrectTokenPositionsInComparisonReport() {
        JPlagResult result = mock(JPlagResult.class);
        JPlagOptions mockOptions = createMockOptions();
        when(result.getOptions()).thenReturn(mockOptions);
        JPlagComparison comparison = mock(JPlagComparison.class);
        String firstID = "first";
        String secondID = "second";
        Submission firstSubmission = createMockSubmission(firstID);
        Submission secondSubmission = createMockSubmission(secondID);
        when(comparison.firstSubmission()).thenReturn(firstSubmission);
        when(comparison.secondSubmission()).thenReturn(secondSubmission);
        Match mockMatch = createMockMatch(0, 2, 0, 1);
        when(comparison.matches()).thenReturn(List.of(mockMatch));

        when(result.getComparisons(1)).thenReturn(List.of(comparison));

        TestableReportWriter resultWriter = new TestableReportWriter();
        Map<String, Map<String, String>> comparisonReportOutput;

        try (MockedStatic<FilePathUtil> mockedFilePathUtil = Mockito.mockStatic(FilePathUtil.class)) {
            mockedFilePathUtil.when(() -> FilePathUtil.getRelativeSubmissionPath(any(), any(), any())).thenReturn(Path.of("file.java"));
            comparisonReportOutput = new ComparisonReportWriter(Submission::getName, resultWriter).writeComparisonReports(result);
        }
        ComparisonReport comparisonReport = (ComparisonReport) resultWriter
                .getJsonEntry(Path.of(ComparisonReportWriter.BASEPATH, comparisonReportOutput.get(firstID).get(secondID)));

        assertEquals(1, comparisonReport.matches().size());

        CodePosition startInFirst = comparisonReport.matches().get(0).startInFirst();
        CodePosition endInFirst = comparisonReport.matches().get(0).endInFirst();
        CodePosition startInSecond = comparisonReport.matches().get(0).startInSecond();
        CodePosition endInSecond = comparisonReport.matches().get(0).endInSecond();

        assertEquals(1, startInFirst.line());
        assertEquals(0, startInFirst.column());
        assertEquals(0, startInFirst.tokenListIndex());

        assertEquals(2, endInFirst.line());
        assertEquals(10, endInFirst.column());
        assertEquals(2, endInFirst.tokenListIndex());

        assertEquals(1, startInSecond.line());
        assertEquals(0, startInSecond.column());
        assertEquals(0, startInSecond.tokenListIndex());

        assertEquals(2, endInSecond.line());
        assertEquals(10, endInSecond.column());
        assertEquals(1, endInSecond.tokenListIndex());
    }

    @Test
    void testCorrectTokenPositionsInBasecodeReport() {
        JPlagResult result = mock(JPlagResult.class);
        JPlagOptions mockOptions = createMockOptions();
        when(result.getOptions()).thenReturn(mockOptions);
        JPlagComparison comparison = mock(JPlagComparison.class);
        String submissionID = "first";
        String basecodeID = "basecode";
        Submission submission = createMockSubmission(submissionID);
        Submission baseCodeSubmission = createMockSubmission(basecodeID);
        when(comparison.firstSubmission()).thenReturn(submission);
        when(comparison.secondSubmission()).thenReturn(baseCodeSubmission);
        Match mockMatch = createMockMatch(0, 2, 0, 1);
        when(comparison.matches()).thenReturn(List.of(mockMatch));
        when(submission.getBaseCodeComparison()).thenReturn(comparison);

        when(result.getComparisons(1)).thenReturn(List.of(comparison));

        TestableReportWriter resultWriter = new TestableReportWriter();
        try (MockedStatic<FilePathUtil> mockedFilePathUtil = Mockito.mockStatic(FilePathUtil.class)) {
            mockedFilePathUtil.when(() -> FilePathUtil.getRelativeSubmissionPath(any(), any(), any())).thenReturn(Path.of("file.java"));
            new BaseCodeReportWriter(Submission::getName, resultWriter).writeBaseCodeReport(result);
        }

        List<BaseCodeMatch> baseCodeMatches = (List<BaseCodeMatch>) resultWriter.getJsonEntry(Path.of("basecode", submissionID + ".json"));

        assertEquals(1, baseCodeMatches.size());

        CodePosition start = baseCodeMatches.get(0).start();
        CodePosition end = baseCodeMatches.get(0).end();

        assertEquals(1, start.line());
        assertEquals(0, start.column());
        assertEquals(0, start.tokenListIndex());

        assertEquals(2, end.line());
        assertEquals(10, end.column());
        assertEquals(2, end.tokenListIndex());
    }

    JPlagOptions createMockOptions() {
        JPlagOptions options = mock(JPlagOptions.class);
        when(options.maximumNumberOfComparisons()).thenReturn(1);
        return options;
    }

    Submission createMockSubmission(String name) {
        Submission submission = mock(Submission.class);
        when(submission.getName()).thenReturn(name);
        List<Token> tokens = List.of(createMockToken(1, 1, 10), createMockToken(2, 1, 10), createMockToken(2, 3, 2), createMockToken(2, 10, 2));
        when(submission.getTokenList()).thenReturn(tokens);
        return submission;
    }

    Match createMockMatch(int startOfFirst, int endOfFirst, int startOfSecond, int endOfSecond) {
        Match match = mock(Match.class);
        when(match.startOfFirst()).thenReturn(startOfFirst);
        when(match.endOfFirst()).thenReturn(endOfFirst);
        when(match.startOfSecond()).thenReturn(startOfSecond);
        when(match.endOfSecond()).thenReturn(endOfSecond);
        return match;
    }

    Token createMockToken(int line, int column, int length) {
        Token token = mock(Token.class);
        when(token.getStartLine()).thenReturn(line);
        when(token.getStartColumn()).thenReturn(column);
        when(token.getEndLine()).thenReturn(line);
        when(token.getEndColumn()).thenReturn(column + length);
        return token;
    }

}
