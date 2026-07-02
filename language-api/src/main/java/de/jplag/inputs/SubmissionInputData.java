package de.jplag.inputs;

import java.util.List;
import java.util.function.Predicate;

public class SubmissionInputData {
    private SubmissionDirectory source;
    private SubmissionFolder folder;
    private boolean isNew;
    private boolean isMultiRoot;

    public SubmissionInputData(SubmissionDirectory source, SubmissionFolder folder, boolean isNew, boolean isMultiRoot) {
        this.source = source;
        this.folder = folder;
        this.isNew = isNew;
        this.isMultiRoot = isMultiRoot;
    }

    public void reRoot() {
        this.folder = this.folder.asRoot();
    }

    public void applyFilter(Predicate<SubmissionFile> filter) {
        this.folder = this.folder.filtered(filter);
    }

    public void useSubdirectory(String path) {
        //TODO separator
        folder = folder.resolveAsRoot(List.of(path.split("/")));
    }

    public boolean isNew() {
        return this.isNew;
    }


    public boolean matchesIdentifier(SubmissionIdentifier identifier) {
        return (identifier.getSubmissionDirectoryName() == null || source.name().equals(identifier.getSubmissionDirectoryName()))
                && folder.name().equals(identifier.getSubmissionName());
    }

    public List<SubmissionFile> listAllFiles() {
        return folder.listAllFiles();
    }

    public boolean isSameSubmission(SubmissionInputData other) {
        return source.equals(other.source) && folder.equals(other.folder);
    }

    public SubmissionFolder getFolder() {
        return folder;
    }

    public SubmissionIdentifier getSubmissionIdentifier() {
        return new SubmissionIdentifier(source.name(), folder.name());
    }

    public String getName() {
        return getSubmissionIdentifier().getName(isMultiRoot);
    }
}
