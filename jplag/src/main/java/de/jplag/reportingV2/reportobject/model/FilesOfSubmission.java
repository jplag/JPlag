package de.jplag.reportingV2.reportobject.model;

import java.util.List;

public class FilesOfSubmission {

	private String file_name;
	private List<String> lines;

	public FilesOfSubmission(String file_name, List<String> file_code) {
		this.file_name = file_name;
		this.lines = file_code;
	}

	public String getFile_name() {
		return file_name;
	}

	public List<String> getLines() {
		return lines;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}
}

