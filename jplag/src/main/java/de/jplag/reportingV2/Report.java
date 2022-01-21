package de.jplag.reportingV2;

import de.jplag.JPlagResult;
import de.jplag.reportingV2.reportobject.model.JPlagReport;

import java.util.List;

//ReportStrategy -> Report

/**
 *  Strategy interface for reporting. A report generator should implement this interface.
 */
public interface Report {

	/**
	 * This function returns a list containing the report objects as simple strings.
	 * The first element is the string of the overview object. Each following element is a string
	 * of a comparison report object.
	 * @param result The result of a JPlag comparison
	 * @return A list containing report objects to string. First element is Overview. Other elements are comparisons.
	 */
	List<String> getReportStrings(JPlagResult result);

	/**
	 * Creates and saves JPlag report files to the disk.
	 * @param result The result of a JPlag comparison.
	 * @return True if the process is successful, otherwise false.
	 */
	boolean saveReport(JPlagResult result);
}
