package de.jplag.inputs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FileSystemSubmissionDirectory implements SubmissionDirectory {
    private File rootFile;
    private SubmissionFolder root;
    private String name;

    public FileSystemSubmissionDirectory(File rootFile, String name) {
        this.rootFile = rootFile;
        this.root = new SubmissionFolder("root", "");
        this.name = name;

        addContents(Collections.emptyList(), rootFile);
    }

    private void addContents(List<String> path, File directory) {
        for (File file : directory.listFiles()) {
            if(file.isDirectory()) {
                List<String> subPath = new ArrayList<>(path);
                subPath.add(file.getName());
                addContents(subPath, file);
            }

            if(file.isFile()) {
                String relativePath = rootFile.toPath().relativize(file.toPath()).toString();
                root.add(path, new FileSystemSubmissionFile(file, relativePath));
            }
        }
    }

    @Override
    public List<SubmissionFolder> resolveSubmissions() {
        return root.getDirectChildrenAsSubmissions();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean contains(File file) {
        return file.getParentFile().equals(rootFile);
    }

    static class FileSystemSubmissionFile implements SubmissionFile {
        private File file;
        private String relativePath;

        public FileSystemSubmissionFile(File file, String relativePath) {
            this.file = file;
            this.relativePath = relativePath;
        }

        @Override
        public InputStream open() throws IOException {
            return new FileInputStream(file);
        }

        @Override
        public URI asUri() {
            return file.toURI();
        }

        @Override
        public SubmissionFile clone(String newPath) {
            return new FileSystemSubmissionFile(file, newPath);
        }

        @Override
        public String name() {
            return file.getName();
        }

        @Override
        public String relativePath() {
            return relativePath;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            FileSystemSubmissionFile that = (FileSystemSubmissionFile) o;
            return Objects.equals(file, that.file) && Objects.equals(relativePath, that.relativePath);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(file);
        }
    }

    public String toString() {
        return String.format("File system{name: %s, path: %s}", name, rootFile.getPath());
    }
}
