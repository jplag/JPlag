package de.jplag.reportingV2.reportobject.model;

public class FilesOfSubmission {

	private String file_name;
	private String file_code;

	public FilesOfSubmission(String file_name, String file_code) {
		this.file_name = file_name;
		this.file_code = file_code;
	}

	public String getFile_name() {
		return file_name;
	}

	public String getFile_code() {
		return file_code;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public void setFile_code(String file_code) {
		this.file_code = file_code;
	}
}

