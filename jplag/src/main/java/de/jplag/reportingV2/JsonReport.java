package de.jplag.reportingV2;

import de.jplag.JPlagResult;
import de.jplag.reportingV2.jsonfactory.JsonFactory;
import de.jplag.reportingV2.reportobject.ReportObjectFactory;
import de.jplag.reportingV2.reportobject.model.JPlagReport;

import java.util.List;

//ReportImplementation -> JsonReport
public class JsonReport implements Report {

	@Override
	public List<String> getReportStrings(JPlagResult result) {
		JPlagReport report = ReportObjectFactory.getReportObject(result);
		return JsonFactory.getJsonStrings(report);
	}

	@Override
	public boolean saveReport(JPlagResult result) {
		JPlagReport report = ReportObjectFactory.getReportObject(result);
		return JsonFactory.saveJsonFiles(report, "./report-viewer/src/files/");
	}

}
