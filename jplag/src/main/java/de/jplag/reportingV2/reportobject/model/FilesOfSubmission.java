package de.jplag.reportingV2.reportobject.model;

import java.util.List;

public class FilesOfSubmission {

    private final String file_name;
    private final List<String> lines;

    public FilesOfSubmission(String file_name, List<String> file_code) {
        this.file_name = file_name;
        this.lines = List.copyOf(file_code);
    }

    public String getFile_name() {
        return file_name;
    }

    public List<String> getLines() {
        return lines;
    }

}
