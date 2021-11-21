package de.jplag.reportingV2.jsonfactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.jplag.reportingV2.reportobject.model.ComparisonReport;
import de.jplag.reportingV2.reportobject.model.JPlagReport;

import java.util.ArrayList;
import java.util.List;

public class JsonFactory {

	public static List<String> generateJsonFiles(JPlagReport jPlagReport) {
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
}
