package de.jplag.reporting2;

import java.util.List;

import de.jplag.JPlagResult;

// ReportStrategy -> Report

/**
 * Strategy interface for reporting. A report generator should implement this interface.
 */
public interface Report {

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
