package de.jplag.reportingV2.reportobject.model;

public class TopComparison {

	private String comparison_name;
	private float match_percentage;

	public TopComparison(String comparison_name, float match_percentage) {
		this.comparison_name = comparison_name;
		this.match_percentage = match_percentage;
	}

	public String getComparison_name() {
		return comparison_name;
	}

	public float getMatch_percentage() {
		return match_percentage;
	}

	public void setComparison_name(String comparison_name) {
		this.comparison_name = comparison_name;
	}

	public void setMatch_percentage(float match_percentage) {
		this.match_percentage = match_percentage;
	}
}
