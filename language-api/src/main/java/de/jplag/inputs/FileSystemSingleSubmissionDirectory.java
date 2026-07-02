package de.jplag.inputs;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class FileSystemSingleSubmissionDirectory implements SubmissionDirectory {
    private File directoryFile;
    private SubmissionFolder folder;
    private String name;

    public FileSystemSingleSubmissionDirectory(File directoryFile, String name) {
        this.directoryFile = directoryFile;
        this.name = name;
        folder = new SubmissionFolder(name, "");
        folder.add(Collections.emptyList(), new FileSystemSubmissionDirectory.FileSystemSubmissionFile(directoryFile, directoryFile.getName()));
    }

    @Override
    public List<SubmissionFolder> resolveSubmissions() {
        return List.of(folder);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean contains(File file) {
        return file.equals(directoryFile);
    }

    @Override
    public String toString() {
        return String.format("Single submission{name: %s, path: %s}", name, directoryFile.getPath());
    }

    public SubmissionIdentifier getSubmissionIdentifer() {
        return new SubmissionIdentifier(name, name);
    }
}
