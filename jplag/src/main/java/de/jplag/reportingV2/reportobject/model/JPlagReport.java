package de.jplag.reportingV2.reportobject.model;

import java.util.List;

public class JPlagReport {
	private OverviewReport overviewReport;
	private List<ComparisonReport> comparisons;

	public JPlagReport( OverviewReport overviewReport, List<ComparisonReport> comparisons) {
		this.overviewReport = overviewReport;
		this.comparisons = comparisons;
	}

	public OverviewReport getOverviewReport() {
		return overviewReport;
	}

	public List<ComparisonReport> getComparisons() {
		return comparisons;
	}
}
