package de.jplag.reporting.reportobject.mapper;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.exceptions.ExitException;
import de.jplag.java.JavaLanguage;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.jsonfactory.ComparisonReportWriter;
import de.jplag.reporting.reportobject.writer.DummyResultWriter;
import de.jplag.reporting.reportobject.writer.JPlagResultWriter;

class ComparisonReportWriterTest {
    private final JPlagResultWriter fileWriter = new DummyResultWriter();
    protected static final String BASE_PATH = Path.of("..", "core", "src", "test", "resources", "de", "jplag", "samples").toString();

    @Test
    void firsLevelOfLookupMapComplete() throws ExitException {
        File submissionDir = new File(String.join("/", BASE_PATH) + "/" + "PartialPlagiarism");
        JPlagResult result = JPlag.run(new JPlagOptions(new JavaLanguage(), Set.of(submissionDir), Set.of()));
        var mapper = new ComparisonReportWriter(Submission::getName, fileWriter);

        Map<String, Map<String, String>> stringMapMap = mapper.writeComparisonReports(result);

        firstLevelOfMapContains(stringMapMap, "A", "B", "C", "D", "E");
    }

    @Test
    void secondLevelOfLookupMapComplete() throws ExitException {
        File submissionDir = new File(String.join("/", BASE_PATH) + "/" + "PartialPlagiarism");
        JPlagResult result = JPlag.run(new JPlagOptions(new JavaLanguage(), Set.of(submissionDir), Set.of()));
        var mapper = new ComparisonReportWriter(Submission::getName, fileWriter);

        Map<String, Map<String, String>> stringMapMap = mapper.writeComparisonReports(result);

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
