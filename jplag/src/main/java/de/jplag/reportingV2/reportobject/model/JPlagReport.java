package de.jplag.reportingV2.reportobject.model;

import java.util.List;

public class JPlagReport {
    private final OverviewReport overviewReport;
    private final List<ComparisonReport> comparisons;

    public JPlagReport(OverviewReport overviewReport, List<ComparisonReport> comparisons) {
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
