package de.jplag.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagResult;

/**
 * Strategy interface for reporting. A report generator should implement this interface.
 */
public interface Report {

    Logger logger = LoggerFactory.getLogger(Report.class);

    /**
     * Creates and saves JPlag report files to the disk.
     * @param result The result of a JPlag comparison.
     * @param path Path to the directory where the report should be saved.
     */
    void saveReport(JPlagResult result, String path);
}
