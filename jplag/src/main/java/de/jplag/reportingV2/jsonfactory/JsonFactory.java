package de.jplag.reportingV2.jsonfactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.jplag.reportingV2.reportobject.model.ComparisonReport;
import de.jplag.reportingV2.reportobject.model.JPlagReport;

/**
 * Factory class, responsible for creating Json strings and writing them to files.
 */
public class JsonFactory {

    /**
     * Uses Jackson to create Json Strings from JPlagReport object.
     * @return A list, first element is Json String of Overview object. The rest elements are Json Strings of Comparison
     * objects.
     */
    public static List<String> getJsonStrings(JPlagReport jPlagReport) {
        List<String> jsonReports = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonReports.add(mapper.writeValueAsString(jPlagReport.getOverviewReport()));
            for (ComparisonReport comparisonReport : jPlagReport.getComparisons()) {
                jsonReports.add(mapper.writeValueAsString(comparisonReport));
            }
        } catch (JsonProcessingException e) {
            System.out.println("Error converting object to json " + e.getMessage());
        }
        return jsonReports;
    }

    /**
     * Creates Json Files for the given JPlagReport and saves them in the given folder.
     * @return A boolean, representing whether the process was successful.
     */
    public static boolean saveJsonFiles(JPlagReport jPlagReport, String folderPath) {
        String sanitizedFolderPath = folderPath;
        if (!folderPath.endsWith("/")) {
            sanitizedFolderPath = folderPath.concat("/");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(sanitizedFolderPath.concat("overview.json")), jPlagReport.getOverviewReport());
            for (ComparisonReport r : jPlagReport.getComparisons()) {
                String name = r.getFirst_submission_id().concat("-").concat(r.getSecond_submission_id()).concat(".json");
                mapper.writeValue(new File(folderPath + name), r);
            }
        } catch (IOException e) {
            System.out.println("Failed to save json files: " + e.getMessage());
            return false;
        }
        return true;
    }
}
