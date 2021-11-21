package de.jplag.reportingV2;

import de.jplag.JPlagResult;

import java.util.List;

public interface ReportStrategy {

	List<String> getJsonStrings(JPlagResult result);
}
