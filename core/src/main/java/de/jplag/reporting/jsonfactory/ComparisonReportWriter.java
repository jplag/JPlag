package de.jplag.reporting.jsonfactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.Token;
import de.jplag.reporting.reportobject.model.ComparisonReport;
import de.jplag.reporting.reportobject.model.Match;

/**
 * Writes {@link ComparisonReport}s of given {@link JPlagResult} to the disk under the specified path. Instantiated with
 * a function that associates a submission to its id.
 */
public class ComparisonReportWriter {

    private final FileWriter fileWriter;
    private final Function<Submission, String> submissionToIdFunction;
    private final Map<String, Map<String, String>> submissionIdToComparisonFileName = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> fileNameCollisions = new ConcurrentHashMap<>();

    public ComparisonReportWriter(Function<Submission, String> submissionToIdFunction, FileWriter fileWriter) {
        this.submissionToIdFunction = submissionToIdFunction;
        this.fileWriter = fileWriter;
    }

    /**
     * Generates detailed ComparisonReport DTO for each comparison in a JPlagResult and writes them to the disk as json
     * files.
     * @param jPlagResult The JPlagResult to generate the comparison reports from. contains information about a comparison
     * @param path The path to write the comparison files to
     * @return Nested map that associates each pair of submissions (by their ids) to their comparison file name. The
     * comparison file name for submission with id id1 and id2 can be fetched by executing get two times:
     * map.get(id1).get(id2). The nested map is symmetrical therefore, both map.get(id1).get(id2) and map.get(id2).get(id1)
     * yield the same result.
     */
    public Map<String, Map<String, String>> writeComparisonReports(JPlagResult jPlagResult, String path) {
        int numberOfComparisons = jPlagResult.getOptions().maximumNumberOfComparisons();
        List<JPlagComparison> comparisons = jPlagResult.getComparisons(numberOfComparisons);
        writeComparisons(path, comparisons);
        return submissionIdToComparisonFileName;
    }

    private void writeComparisons(String path, List<JPlagComparison> comparisons) {
        comparisons.parallelStream().forEach(comparison -> {
            String firstSubmissionId = submissionToIdFunction.apply(comparison.firstSubmission());
            String secondSubmissionId = submissionToIdFunction.apply(comparison.secondSubmission());
            String fileName = generateComparisonName(firstSubmissionId, secondSubmissionId);
            addToLookUp(firstSubmissionId, secondSubmissionId, fileName);
            var comparisonReport = new ComparisonReport(firstSubmissionId, secondSubmissionId, comparison.similarity(),
                    convertMatchesToReportMatches(comparison));
            fileWriter.saveAsJSON(comparisonReport, path, fileName);
        });
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

        Comparator<? super Token> lineComparator = (first, second) -> first.getLine() - second.getLine();

        Token startOfFirst = tokensFirst.stream().min(lineComparator).orElseThrow();
        Token endOfFirst = tokensFirst.stream().max(lineComparator).orElseThrow();
        Token startOfSecond = tokensSecond.stream().min(lineComparator).orElseThrow();
        Token endOfSecond = tokensSecond.stream().max(lineComparator).orElseThrow();

        return new Match(relativizedFilePath(startOfFirst.getFile(), comparison.firstSubmission()),
                relativizedFilePath(startOfSecond.getFile(), comparison.secondSubmission()), startOfFirst.getLine(), endOfFirst.getLine(),
                startOfSecond.getLine(), endOfSecond.getLine(), match.length());
    }

    private String relativizedFilePath(File file, Submission submission) {
        if (file.toPath().equals(submission.getRoot().toPath())) {
            return Path.of(submissionToIdFunction.apply(submission), submissionToIdFunction.apply(submission)).toString();
        }
        return Path.of(submissionToIdFunction.apply(submission), submission.getRoot().toPath().relativize(file.toPath()).toString()).toString();
    }

}
