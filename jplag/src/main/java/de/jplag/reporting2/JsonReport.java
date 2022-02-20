package de.jplag.reporting2;

import java.io.File;
import java.util.List;

import de.jplag.JPlagResult;
import de.jplag.reporting2.jsonfactory.JsonFactory;
import de.jplag.reporting2.reportobject.ReportObjectFactory;
import de.jplag.reporting2.reportobject.model.JPlagReport;

// ReportImplementation -> JsonReport

/**
 * A report generator which reports the JPlagResult in Json format.
 */
public class JsonReport implements Report {

    @Override
    public List<String> getReportStrings(JPlagResult result) {
        JPlagReport report = ReportObjectFactory.getReportObject(result);
        return JsonFactory.getJsonStrings(report);
    }

    @Override
    public boolean saveReport(JPlagResult result, String path) {
        JPlagReport report = ReportObjectFactory.getReportObject(result);
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                System.out.println("Failed to create dir.");
            }
        }
        return JsonFactory.saveJsonFiles(report, path);
    }

}
