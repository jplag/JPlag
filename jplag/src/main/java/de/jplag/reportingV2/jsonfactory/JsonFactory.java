package de.jplag.reportingV2.jsonfactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.jplag.JPlagResult;
import de.jplag.reportingV2.reportobject.model.ComparisonReport;
import de.jplag.reportingV2.reportobject.model.JPlagReport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonFactory {

	public static List<String> getJsonStrings(JPlagReport jPlagReport) {
		List<String> jsonReports = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			jsonReports.add(mapper.writeValueAsString(jPlagReport.getOverviewReport()));
			for (ComparisonReport comparisonReport: jPlagReport.getComparisons()) {
				jsonReports.add(mapper.writeValueAsString(comparisonReport));
			}
		} catch ( JsonProcessingException e) {
			System.out.println("Error converting object to json " + e.getMessage());
		}
		return jsonReports;
	}

	public static boolean saveJsonFiles(JPlagReport jPlagReport, String folderPath ) {
		String sanitizedFolderPath = folderPath;
		if ( !folderPath.endsWith("/") ) { sanitizedFolderPath = folderPath.concat("/"); }
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue( new File( sanitizedFolderPath.concat("overview.json") ), jPlagReport.getOverviewReport() );
			for (ComparisonReport r : jPlagReport.getComparisons()) {
				String name = r.getFirst_submission_id().concat("-").concat(r.getSecond_submission_id()).concat(".json");
				mapper.writeValue(new File(folderPath + name), r);
			}
		} catch ( IOException e) {
			System.out.println("Failed to save json files: " + e.getMessage());
			return false;
		}
		return true;
	}
}
