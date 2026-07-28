package de.jplag;

import de.jplag.inputs.SubmissionFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DummySubmissionFile implements SubmissionFile {
    private File file;
    private String relativePath;

    public DummySubmissionFile(File file, String relativePath) {
        this.file = file;
        this.relativePath = relativePath;
    }

    @Override
    public InputStream open() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public URI asUri() throws IOException {
        return file.toURI();
    }

    @Override
    public SubmissionFile clone(String newPath) {
        return new DummySubmissionFile(file, newPath);
    }

    @Override
    public String name() {
        return file.getName();
    }

    @Override
    public String relativePath() {
        return relativePath;
    }
}
