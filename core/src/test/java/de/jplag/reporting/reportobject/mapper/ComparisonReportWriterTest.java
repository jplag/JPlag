package de.jplag.reporting.reportobject.mapper;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.TestBase;
import de.jplag.exceptions.ExitException;
import de.jplag.reporting.jsonfactory.ComparisonReportWriter;
import de.jplag.reporting.jsonfactory.DummyWriter;
import de.jplag.reporting.jsonfactory.FileWriter;

public class ComparisonReportWriterTest extends TestBase {
    private final FileWriter fileWriter = new DummyWriter();

    @Test
    public void firsLevelOfLookupMapComplete() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("PartialPlagiarism");
        var mapper = new ComparisonReportWriter(Submission::getName, fileWriter);

        Map<String, Map<String, String>> stringMapMap = mapper.writeComparisonReports(result, "");

        firstLevelOfMapContains(stringMapMap, "A", "B", "C", "D", "E");
    }

    @Test
    public void secondLevelOfLookupMapComplete() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("PartialPlagiarism");
        var mapper = new ComparisonReportWriter(Submission::getName, fileWriter);

        Map<String, Map<String, String>> stringMapMap = mapper.writeComparisonReports(result, "");

        secondLevelOfMapContains(stringMapMap, "A", "B", "C", "D", "E");
        secondLevelOfMapContains(stringMapMap, "B", "A", "C", "D", "E");
        secondLevelOfMapContains(stringMapMap, "C", "B", "A", "D", "E");
        secondLevelOfMapContains(stringMapMap, "D", "B", "C", "A", "E");
        secondLevelOfMapContains(stringMapMap, "E", "B", "C", "D", "A");
    }

    private void secondLevelOfMapContains(Map<String, Map<String, String>> stringMapMap, String firstLevelSubmission,
            String... secondLevelSubmissions) {
        for (String secondLevelSubmission : secondLevelSubmissions) {
            Assertions.assertNotNull(stringMapMap.get(firstLevelSubmission).get(secondLevelSubmission));
            Assertions.assertFalse(stringMapMap.get(firstLevelSubmission).get(secondLevelSubmission).isEmpty());

        }

    }

    private void firstLevelOfMapContains(Map<String, Map<String, String>> stringMapMap, String... names) {
        for (String name : names) {
            Assertions.assertNotNull(stringMapMap.get(name));
        }
    }
}
