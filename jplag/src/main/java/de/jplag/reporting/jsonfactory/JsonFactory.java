package de.jplag.reporting.jsonfactory;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.jplag.reporting.reportobject.model.ComparisonReport;
import de.jplag.reporting.reportobject.model.JPlagReport;

/**
 * Factory class, responsible for creating Json strings and writing them to files.
 */
public class JsonFactory {

    private static final Logger logger = LoggerFactory.getLogger(JsonFactory.class);

    /**
     * Creates Json Files for the given JPlagReport and saves them in the given folder.
     */
    public static void saveJsonFiles(JPlagReport jPlagReport, String folderPath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Path.of(folderPath, "overview.json").toFile(), jPlagReport.overviewReport());
            mapper.writeValue(Path.of(folderPath, "lookUpTable.json").toFile(), jPlagReport.lineLookUpTable());
            for (ComparisonReport report : jPlagReport.comparisons()) {
                String name = report.firstSubmissionId().concat("-").concat(report.secondSubmissionId()).concat(".json");
                mapper.writeValue(Path.of(folderPath, name).toFile(), report);
            }
        } catch (IOException e) {
            logger.error("Failed to save json files: " + e.getMessage(), e);
        }
    }
}
