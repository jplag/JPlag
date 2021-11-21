package de.jplag.reportingV2.reportobject.model;

public class Match {

	private String first_file_name;
	private String second_file_name;
	private int start_in_first;
	private int end_in_first;
	private int start_in_second;
	private int end_in_second;

	public Match(String first_file_name,
				 String second_file_name,
				 int start_in_first,
				 int end_in_first,
				 int start_in_second,
				 int end_in_second) {

		this.first_file_name = first_file_name;
		this.second_file_name = second_file_name;
		this.start_in_first = start_in_first;
		this.end_in_first = end_in_first;
		this.start_in_second = start_in_second;
		this.end_in_second = end_in_second;
	}

	public String getFirst_file_name() {
		return first_file_name;
	}

	public String getSecond_file_name() {
		return second_file_name;
	}

	public int getStart_in_first() {
		return start_in_first;
	}

	public int getEnd_in_first() {
		return end_in_first;
	}

	public int getStart_in_second() {
		return start_in_second;
	}

	public int getEnd_in_second() {
		return end_in_second;
	}

	public void setFirst_file_name(String first_file_name) {
		this.first_file_name = first_file_name;
	}

	public void setSecond_file_name(String second_file_name) {
		this.second_file_name = second_file_name;
	}

	public void setStart_in_first(int start_in_first) {
		this.start_in_first = start_in_first;
	}

	public void setEnd_in_first(int end_in_first) {
		this.end_in_first = end_in_first;
	}

	public void setStart_in_second(int start_in_second) {
		this.start_in_second = start_in_second;
	}

	public void setEnd_in_second(int end_in_second) {
		this.end_in_second = end_in_second;
	}
}
