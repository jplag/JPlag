package de.jplag.reporting.jsonfactory;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.Token;
import de.jplag.options.SimilarityMetric;
import de.jplag.reporting.FilePathUtil;
import de.jplag.reporting.reportobject.model.CodePosition;
import de.jplag.reporting.reportobject.model.ComparisonReport;
import de.jplag.reporting.reportobject.model.Match;
import de.jplag.reporting.reportobject.writer.JPlagResultWriter;

/**
 * Writes {@link ComparisonReport}s of given {@link JPlagResult} to the disk under the specified path. Instantiated with
 * a function that associates a submission to its id.
 */
public class ComparisonReportWriter {

    private final JPlagResultWriter resultWriter;
    private final Function<Submission, String> submissionToIdFunction;
    private final Map<String, Map<String, String>> submissionIdToComparisonFileName = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> fileNameCollisions = new ConcurrentHashMap<>();
    /**
     * The base path used for storing comparison data.
     */
    public static final String BASEPATH = "comparisons";
    private static final SimilarityMetric[] EXPORTED_SIMILARITY_METRICS = new SimilarityMetric[] {SimilarityMetric.AVG, SimilarityMetric.MAX,
            SimilarityMetric.LONGEST_MATCH, SimilarityMetric.MAXIMUM_LENGTH};

    /**
     * Constructs a ComparisonReportWriter with the specified submission ID mapping function and a result writer to handle
     * the output.
     * @param submissionToIdFunction function to convert Submission objects to their unique string IDs
     * @param resultWriter the writer responsible for producing the JPlag result output
     */
    public ComparisonReportWriter(Function<Submission, String> submissionToIdFunction, JPlagResultWriter resultWriter) {
        this.submissionToIdFunction = submissionToIdFunction;
        this.resultWriter = resultWriter;
    }

    /**
     * Generates detailed ComparisonReport DTO for each comparison in a JPlagResult and writes them to the disk as json
     * files.
     * @param jPlagResult The JPlagResult to generate the comparison reports from. contains information about a comparison
     * @return Nested map that associates each pair of submissions (by their ids) to their comparison file name. The
     * comparison file name for submission with id id1 and id2 can be fetched by executing get two times:
     * map.get(id1).get(id2). The nested map is symmetrical therefore, both map.get(id1).get(id2) and map.get(id2).get(id1)
     * yield the same result.
     */
    public Map<String, Map<String, String>> writeComparisonReports(JPlagResult jPlagResult) {
        int numberOfComparisons = jPlagResult.getOptions().maximumNumberOfComparisons();
        List<JPlagComparison> comparisons = jPlagResult.getComparisons(numberOfComparisons);
        writeComparisons(comparisons);
        return submissionIdToComparisonFileName;
    }

    private void writeComparisons(List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            String firstSubmissionId = submissionToIdFunction.apply(comparison.firstSubmission());
            String secondSubmissionId = submissionToIdFunction.apply(comparison.secondSubmission());
            String fileName = generateComparisonName(firstSubmissionId, secondSubmissionId);
            addToLookUp(firstSubmissionId, secondSubmissionId, fileName);
            var comparisonReport = new ComparisonReport(firstSubmissionId, secondSubmissionId, createSimilarityMap(comparison),
                    convertMatchesToReportMatches(comparison), comparison.similarityOfFirst(), comparison.similarityOfSecond());
            resultWriter.addJsonEntry(comparisonReport, Path.of(BASEPATH, fileName));
        }
    }

    private Map<String, Double> createSimilarityMap(JPlagComparison comparison) {
        Map<String, Double> result = new HashMap<>();
        for (SimilarityMetric metric : EXPORTED_SIMILARITY_METRICS) {
            result.put(metric.name(), metric.applyAsDouble(comparison));
        }
        return result;
    }

    private void addToLookUp(String firstSubmissionId, String secondSubmissionId, String fileName) {
        writeToMap(secondSubmissionId, firstSubmissionId, fileName);
        writeToMap(firstSubmissionId, secondSubmissionId, fileName);
    }

    private void writeToMap(String id1, String id2, String comparisonFileName) {
        submissionIdToComparisonFileName.putIfAbsent(id1, new ConcurrentHashMap<>());
        submissionIdToComparisonFileName.get(id1).put(id2, comparisonFileName);
    }

    private String generateComparisonName(String firstSubmissionId, String secondSubmissionId) {
        String name = concatenate(firstSubmissionId, secondSubmissionId);
        AtomicInteger collisionCounter = fileNameCollisions.putIfAbsent(name, new AtomicInteger(1));
        if (collisionCounter != null) {
            name = concatenate(firstSubmissionId, secondSubmissionId, collisionCounter.incrementAndGet());
        }
        return name;
    }

    private String concatenate(String firstSubmissionId, String secondSubmissionId, long index) {
        return firstSubmissionId.concat("-").concat(secondSubmissionId).concat(index > 0 ? "-" + index : "").concat(".json");
    }

    private String concatenate(String firstSubmissionId, String secondSubmissionId) {
        return concatenate(firstSubmissionId, secondSubmissionId, 0);
    }

    private List<Match> convertMatchesToReportMatches(JPlagComparison comparison) {
        return comparison.matches().stream().map(match -> convertMatchToReportMatch(comparison, match)).toList();
    }

    private Match convertMatchToReportMatch(JPlagComparison comparison, de.jplag.Match match) {
        List<Token> tokensFirst = comparison.firstSubmission().getTokenList().subList(match.startOfFirst(), match.endOfFirst() + 1);
        List<Token> tokensSecond = comparison.secondSubmission().getTokenList().subList(match.startOfSecond(), match.endOfSecond() + 1);

        Comparator<? super Token> lineStartComparator = Comparator.comparingInt(Token::getStartLine).thenComparingInt(Token::getStartColumn);
        Comparator<? super Token> lineEndComparator = Comparator.comparingInt(Token::getEndLine).thenComparingInt(Token::getEndColumn);

        Token startOfFirst = tokensFirst.stream().min(lineStartComparator).orElseThrow();
        Token endOfFirst = tokensFirst.stream().max(lineEndComparator).orElseThrow();
        Token startOfSecond = tokensSecond.stream().min(lineStartComparator).orElseThrow();
        Token endOfSecond = tokensSecond.stream().max(lineEndComparator).orElseThrow();

        String firstFileName = FilePathUtil.getRelativeSubmissionPath(startOfFirst.getFile(), comparison.firstSubmission(), submissionToIdFunction)
                .toString();
        String secondFileName = FilePathUtil.getRelativeSubmissionPath(startOfSecond.getFile(), comparison.secondSubmission(), submissionToIdFunction)
                .toString();

        CodePosition startInFirst = new CodePosition(startOfFirst.getStartLine(), startOfFirst.getStartColumn() - 1, match.startOfFirst());
        CodePosition endInFirst = new CodePosition(endOfFirst.getEndLine(), endOfFirst.getEndColumn() - 1, match.endOfFirst());

        CodePosition startInSecond = new CodePosition(startOfSecond.getStartLine(), startOfSecond.getStartColumn() - 1, match.startOfSecond());
        CodePosition endInSecond = new CodePosition(endOfSecond.getEndLine(), endOfSecond.getEndColumn() - 1, match.endOfSecond());

        return new Match(firstFileName, secondFileName, startInFirst, endInFirst, startInSecond, endInSecond, match.lengthOfFirst(),
                match.lengthOfSecond());
    }

}
