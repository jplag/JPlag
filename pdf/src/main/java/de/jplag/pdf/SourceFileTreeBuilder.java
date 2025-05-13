package de.jplag.pdf;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.Submission;

import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Text;

public class SourceFileTreeBuilder {
    private static final int INDENT_DEPTH = 4;
    private FileEntry root;

    public SourceFileTreeBuilder(Submission submission) {
        this.root = new FileEntry("root", new HashMap<>(), null);

        for (File file : submission.getFiles()) {
            this.addFile(submission.getRoot(), file);
        }

        this.root.flatten();
    }

    private void addFile(File root, File file) {
        Path relativePath = root.toPath().relativize(file.toPath());
        this.root.addFile(relativePath, file);
    }

    public List<Text> buildText() {
        List<Text> texts = new ArrayList<>();
        this.root.addTo(texts, 0);
        return texts;
    }

    private static final class FileEntry {
        private String name;
        private Map<String, FileEntry> children;
        private File file;

        private FileEntry(String name, Map<String, FileEntry> children, File file) {
            this.name = name;
            this.children = children;
            this.file = file;
        }

        public void addFile(Path relativePath, File file) {
            if (relativePath.getNameCount() > 0) {
                if (this.children.containsKey(relativePath.getName(0).toString())) {
                    this.children.get(relativePath.getName(0).toString()).addFile(relativePath.subpath(1, relativePath.getNameCount()), file);
                } else {
                    FileEntry child = new FileEntry(relativePath.getName(0).toString(), new HashMap<>(), null);
                    if (relativePath.getNameCount() > 1) {
                        child.addFile(relativePath.subpath(1, relativePath.getNameCount()), file);
                    } else {
                        child.setFile(file);
                    }
                    this.children.put(relativePath.getName(0).toString(), child);
                }
            } else {
                this.file = file;
            }
        }

        private void setFile(File file) {
            this.file = file;
        }

        public void flatten() {
            if (this.children.size() == 1 && this.children.values().stream().findFirst().get().file == null) {
                String childName = this.children.keySet().stream().toList().get(0);
                this.name = this.name + "/" + childName;
                this.children = this.children.get(childName).children;
                this.flatten();
            } else {
                this.children.values().forEach(FileEntry::flatten);
            }
        }

        public void addTo(List<Text> texts, int depth) {
            if (depth != 0) {
                String indent = " ".repeat(depth * INDENT_DEPTH);
                texts.add(new Text("\u200b" + indent + "-> "));
            }

            if (this.children.isEmpty()) {
                texts.add(new Link(this.name, PdfAction.createGoTo(PathIdLookup.getInstance().getIdFor(file))).setBorder(null));
            } else {
                texts.add(new Text(this.name));
            }
            texts.add(new Text("\n"));

            this.children.values().forEach(child -> child.addTo(texts, depth + 1));
        }
    }
}
