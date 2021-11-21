package de.jplag.reportingV2;

import de.jplag.JPlagResult;
import de.jplag.reportingV2.jsonfactory.JsonFactory;
import de.jplag.reportingV2.reportobject.ReportObjectFactory;
import de.jplag.reportingV2.reportobject.model.JPlagReport;

import java.util.List;

public class ReportImplementation  implements ReportStrategy{

	@Override
	public List<String> getJsonStrings(JPlagResult result) {
		JPlagReport report = ReportObjectFactory.getReportObject(result);
		return JsonFactory.generateJsonFiles(report);
	}
}
