package de.jplag.reporting.reportobject.model;

import java.util.List;

/**
 * Holds general information about the generated report, including the secondary metrics that are included.
 * @param secondaryMetrics The identifiers of the secondary metrics included in the report.
 */
public record ReportInformation(List<String> secondaryMetrics) {
}
