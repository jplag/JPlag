package de.jplag.inputs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class SubmissionFolder implements SubmissionDirectoryComponent {
    List<SubmissionFile> files;
    Map<String, SubmissionFolder> directories;

    private String name;
    private String path;

    public SubmissionFolder(String name, String path) {
        this.files = new ArrayList<>();
        this.directories = new HashMap<>();

        this.name = name;
        this.path = path;
    }

    public void add(List<String> pathElements, SubmissionFile file) {
        if (pathElements.isEmpty()) {
            files.add(file);
        } else {
            String path;
            if(this.path.isEmpty()) {
                path = pathElements.get(0);
            } else {
                path = this.path + "/" + pathElements.get(0);
            }

            directories.computeIfAbsent(pathElements.get(0), (_) -> new SubmissionFolder(pathElements.get(0), path));
            directories.get(pathElements.get(0)).add(pathElements.subList(1, pathElements.size()), file);
        }
    }

    public SubmissionFolder resolveAsRoot(List<String> path) {
        if(path.isEmpty()) {
            return this.asRoot();
        }

        return directories.get(path.get(0)).resolveAsRoot(path.subList(1, path.size()));
    }

    public List<SubmissionFile> listAllFiles() {
        List<SubmissionFile> collector = new ArrayList<>();
        collectFiles(collector);
        return collector;
    }

    private void collectFiles(List<SubmissionFile> collector) {
        collector.addAll(files);
        for (SubmissionFolder directory : directories.values()) {
            directory.collectFiles(collector);
        }
    }

    public SubmissionFolder filtered(Predicate<SubmissionFile> filter) {
        SubmissionFolder filtered = new SubmissionFolder(name, path);
        for (SubmissionFile file : files) {
            if (filter.test(file)) {
                filtered.files.add(file);
            }
        }
        for (SubmissionFolder folder : directories.values()) {
            filtered.directories.put(folder.name(), folder.filtered(filter));
        }

        return filtered;
    }

    public SubmissionFolder asRoot() {
        return this.withPathPrefixRemoved(this.path);
    }

    private SubmissionFolder withPathPrefixRemoved(String prefix) {
        SubmissionFolder reRooted = new SubmissionFolder(name, path.substring(prefix.length()));
        for (SubmissionFile file : files) {
            reRooted.files.add(file.clone(reRooted.path + "/" + file.name()));
        }
        for (SubmissionFolder folder : directories.values()) {
            reRooted.directories.put(folder.name(), folder.withPathPrefixRemoved(prefix));
        }
        return reRooted;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String relativePath() {
        return this.path;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SubmissionFolder that = (SubmissionFolder) o;
        return Objects.equals(files, that.files) && Objects.equals(directories, that.directories) && Objects.equals(name, that.name) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(files, directories, name, path);
    }

    public Map<String, SubmissionFolder> getDirectories() {
        return directories;
    }

    public List<SubmissionFile> getFiles() {
        return files;
    }

    /*package-private*/ List<SubmissionFolder> getDirectChildrenAsSubmissions() {
        List<SubmissionFolder> folders = new ArrayList<>();
        files.forEach(file -> {
            SubmissionFolder virtualRoot = new SubmissionFolder(file.name(), "");
            virtualRoot.add(Collections.emptyList(), file);
            folders.add(virtualRoot);
        });
        for (SubmissionFolder folder : directories.values()) {
            folders.add(folder.asRoot());
        }
        return folders;
    }

    public static SubmissionFolder makeVirtualRoot(SubmissionFile file) {
        SubmissionFolder virtualRoot = new SubmissionFolder("virtualRoot", "");
        virtualRoot.add(Collections.emptyList(), file);
        return virtualRoot;
    }
}
