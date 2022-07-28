package de.jplag.reporting.reportobject.model;

import java.util.List;

public record JPlagReport(OverviewReport overviewReport, List<ComparisonReport> comparisons) {
    public JPlagReport(OverviewReport overviewReport, List<ComparisonReport> comparisons) {
        this.overviewReport = overviewReport;
        this.comparisons = List.copyOf(comparisons);
    }
}
