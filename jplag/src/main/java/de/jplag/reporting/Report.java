package de.jplag.reporting;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagResult;

// ReportStrategy -> Report

/**
 * Strategy interface for reporting. A report generator should implement this interface.
 */
public interface Report {

    Logger logger = LoggerFactory.getLogger(Report.class);

    /**
     * This function returns a list containing the report objects as simple strings. The first element is the string of the
     * overview object. Each following element is a string of a comparison report object.
     * @param result The result of a JPlag comparison
     * @return A list containing report objects to string. First element is Overview. Other elements are comparisons.
     */
    List<String> getReportStrings(JPlagResult result);

    /**
     * Creates and saves JPlag report files to the disk.
     * @param result The result of a JPlag comparison.
     * @param path Path to the directory where the report should be saved.
     * @return True if the process is successful, otherwise false.
     */
    boolean saveReport(JPlagResult result, String path);
}
