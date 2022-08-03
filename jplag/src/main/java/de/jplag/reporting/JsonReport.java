package de.jplag.reporting;

import java.io.File;

import de.jplag.JPlagResult;
import de.jplag.reporting.jsonfactory.JsonFactory;
import de.jplag.reporting.reportobject.ReportObjectFactory;
import de.jplag.reporting.reportobject.model.JPlagReport;

/**
 * A report generator which reports the JPlagResult in Json format.
 */
public class JsonReport implements Report {

    @Override
    public void saveReport(JPlagResult result, String path) {
        JPlagReport report = ReportObjectFactory.getReportObject(result);
        File directory = new File(path);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                logger.error("Failed to create dir.");
            }
        }
        JsonFactory.saveJsonFiles(report, path);
    }

}
