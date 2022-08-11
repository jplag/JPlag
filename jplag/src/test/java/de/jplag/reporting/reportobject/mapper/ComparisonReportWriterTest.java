package de.jplag.reporting.reportobject.mapper;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.TestBase;
import de.jplag.exceptions.ExitException;
import de.jplag.reporting.jsonfactory.ComparisonReportWriter;

public class ComparisonReportWriterTest extends TestBase {

    @Test
    public void test() throws ExitException {
        JPlagResult result = runJPlagWithDefaultOptions("PartialPlagiarism");
        var mapper = new ComparisonReportWriter(Submission::getName);

        Map<String, Map<String, String>> stringMapMap = mapper.writeComparisonReports(result, "");

        firstLevelOfMapContains(stringMapMap, "A", "B", "C", "D", "E");
    }

    private void firstLevelOfMapContains(Map<String, Map<String, String>> stringMapMap, String... names) {
        for (String name : names) {
            Assertions.assertNotNull(stringMapMap.get(name));
        }
    }
}
