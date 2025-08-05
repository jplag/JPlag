package de.jplag.reporting.jsonfactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.Token;
import de.jplag.reporting.FilePathUtil;
import de.jplag.reporting.reportobject.model.BaseCodeMatch;
import de.jplag.reporting.reportobject.model.CodePosition;
import de.jplag.reporting.reportobject.writer.JPlagResultWriter;

/**
 * Writes the comparisons of each Submission to the basecode in its own file.
 */
public class BaseCodeReportWriter {

    private final JPlagResultWriter resultWriter;
    private final Function<Submission, String> submissionToIdFunction;
    private static final String BASEPATH = "basecode";

    /**
     * Creates a new BaseCodeReportWriter.
     * @param submissionToIdFunction Function for translating a submission to a unique id.
     * @param resultWriter Writer used for writing the result.
     */
    public BaseCodeReportWriter(Function<Submission, String> submissionToIdFunction, JPlagResultWriter resultWriter) {
        this.submissionToIdFunction = submissionToIdFunction;
        this.resultWriter = resultWriter;
    }

    /**
     * Writes the basecode of each submission in the result into its own file in the result writer.
     * @param jPlagResult The result containing the submissions.
     */
    public void writeBaseCodeReport(JPlagResult jPlagResult) {
        Set<Submission> submissions = new HashSet<>();

        int numberOfComparisons = jPlagResult.getOptions().maximumNumberOfComparisons();
        List<JPlagComparison> comparisons = jPlagResult.getComparisons(numberOfComparisons);
        for (JPlagComparison comparison : comparisons) {
            submissions.add(comparison.firstSubmission());
            submissions.add(comparison.secondSubmission());
        }
        submissions.forEach(this::writeBaseCodeFile);
    }

    private void writeBaseCodeFile(Submission submission) {
        List<BaseCodeMatch> matches = new ArrayList<>();
        if (submission.getBaseCodeComparison() != null) {
            JPlagComparison baseCodeComparison = submission.getBaseCodeComparison();
            boolean takeLeft = baseCodeComparison.firstSubmission().equals(submission);
            matches.addAll(baseCodeComparison.matches().stream().map(match -> convertToBaseCodeMatch(submission, match, takeLeft)).toList());
            matches.addAll(
                    baseCodeComparison.commentMatches().stream().map(match -> convertToCommentBaseCodeMatch(submission, match, takeLeft)).toList());
        }
        resultWriter.addJsonEntry(matches, Path.of(BASEPATH, submissionToIdFunction.apply(submission).concat(".json")));
    }

    private BaseCodeMatch convertToCommentBaseCodeMatch(Submission submission, Match match, boolean takeLeft) {
        List<Token> tokens = submission.getComments().subList(takeLeft ? match.startOfFirst() : match.startOfSecond(),
                (takeLeft ? match.endOfFirst() : match.endOfSecond()) + 1);
        return convertTokenListToBaseCodeMatch(tokens, submission, match, takeLeft);
    }

    private BaseCodeMatch convertToBaseCodeMatch(Submission submission, Match match, boolean takeLeft) {
        List<Token> tokens = submission.getTokenList().subList(takeLeft ? match.startOfFirst() : match.startOfSecond(),
                (takeLeft ? match.endOfFirst() : match.endOfSecond()) + 1);
        return convertTokenListToBaseCodeMatch(tokens, submission, match, takeLeft);
    }

    private BaseCodeMatch convertTokenListToBaseCodeMatch(List<Token> tokens, Submission submission, Match match, boolean takeLeft) {
        Comparator<? super Token> lineStartComparator = Comparator.comparingInt(Token::getStartLine).thenComparingInt(Token::getStartColumn);
        Comparator<? super Token> lineEndComparator = Comparator.comparingInt(Token::getEndLine).thenComparingInt(Token::getEndColumn);
        Token start = tokens.stream().min(lineStartComparator).orElseThrow();
        Token end = tokens.stream().max(lineEndComparator).orElseThrow();

        CodePosition startPosition = new CodePosition(start.getStartLine(), start.getStartColumn() - 1,
                takeLeft ? match.startOfFirst() : match.startOfSecond());
        CodePosition endPosition = new CodePosition(end.getEndLine(), end.getEndColumn() - 1, takeLeft ? match.endOfFirst() : match.endOfSecond());

        int length = takeLeft ? match.lengthOfFirst() : match.lengthOfSecond();

        return new BaseCodeMatch(FilePathUtil.getRelativeSubmissionPath(start.getFile(), submission, submissionToIdFunction).toString(),
                startPosition, endPosition, length);
    }
}