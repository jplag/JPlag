package de.jplag.reportingV2;

import de.jplag.JPlagResult;
import de.jplag.reportingV2.reportobject.model.JPlagReport;

import java.util.List;

//ReportStrategy -> Report
public interface Report {

	List<String> getReportStrings(JPlagResult result);
	boolean saveReport(JPlagResult result);
}
