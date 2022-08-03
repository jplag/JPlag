package de.jplag.reporting.reportobject.model;

import java.util.List;
import java.util.Map;

public record JPlagReport(OverviewReport overviewReport, List<ComparisonReport> comparisons, Map<Long, String> lineLookUpTable) {
}
