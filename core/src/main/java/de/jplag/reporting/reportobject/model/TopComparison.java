package de.jplag.reporting.reportobject.model;

import java.util.Map;

public record TopComparison(String firstSubmission, String secondSubmission, Map<String, Double> similarities) {
}
