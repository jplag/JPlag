package de.jplag.inputs;

import de.jplag.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileSubmissionDirectory implements SubmissionDirectory {
    private static final String DIRECTORY_SEPARATOR = "/";
    private ZipFile zipFile;
    private SubmissionFolder root;
    private File zipFileLocation;
    private String name;

    public ZipFileSubmissionDirectory(File zip, String name) throws IOException {
        root = new SubmissionFolder("root", "");
        this.zipFile = new ZipFile(zip);
        this.zipFileLocation = zip;
        this.name = name;

        Enumeration<? extends ZipEntry> entryEnumeration = zipFile.entries();
        for (ZipEntry entry = entryEnumeration.nextElement(); entryEnumeration.hasMoreElements(); entry = entryEnumeration.nextElement()) {
            if (!entry.isDirectory()) {
                String zipName = entry.getName();
                String[] parts = zipName.split(DIRECTORY_SEPARATOR);
                List<String> path = new ArrayList<>(List.of(parts));
                path.remove(path.size() - 1);
                String fileName = parts[parts.length - 1];
                root.add(path, new ZipContainedFile(entry, fileName, zipName));
            }
        }

        if (root.getFiles().isEmpty() && root.getDirectories().size() == 1) {
            root = root.getDirectories().values().stream().findAny().get();
            root = root.asRoot();
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
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ZipFileSubmissionDirectory that = (ZipFileSubmissionDirectory) o;
        return Objects.equals(zipFileLocation, that.zipFileLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(zipFileLocation);
    }

    private class ZipContainedFile implements SubmissionFile {
        private ZipEntry entry;
        private String name;
        private String path;

        public ZipContainedFile(ZipEntry entry, String name, String path) {
            this.entry = entry;
            this.name = name;
            this.path = path;
        }

        @Override
        public InputStream open() throws IOException {
            return zipFile.getInputStream(entry);
        }

        @Override
        public URI asUri() throws IOException {
            int dotIndex = name.lastIndexOf(".");
            String prefix = "";
            String suffix = name;
            if (dotIndex != -1) {
                prefix = name.substring(0, dotIndex);
                suffix = name.substring(dotIndex + 1);
            }
            File target = Files.createTempFile(prefix, suffix).toFile();
            open().transferTo(new FileOutputStream(target)); //TODO try
            return target.toURI();
        }

        @Override
        public SubmissionFile clone(String newPath) {
            return new ZipContainedFile(entry, name, newPath);
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String relativePath() {
            return path;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return false;
            }
            if (!(o instanceof ZipContainedFile)) {
                return false;
            }
            ZipContainedFile other = (ZipContainedFile) o;

            return Objects.equals(this.getDirectory(), other.getDirectory()) &&
                    Objects.equals(this.path, other.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getDirectory(), path);
        }

        private ZipFileSubmissionDirectory getDirectory() {
            return ZipFileSubmissionDirectory.this;
        }
    }

    public String toString() {
        return String.format("Zip file{name: %s, path: %s}", name, zipFileLocation.getPath());
    }
}
