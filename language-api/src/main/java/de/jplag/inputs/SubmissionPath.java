package de.jplag.inputs;

import java.util.Arrays;

public class SubmissionPath {
    private String[] elements;

    public SubmissionPath() {
        this.elements = new String[0];
    }

    public SubmissionPath(String[] elements) {
        this.elements = elements;
    }

    public SubmissionPath resolve(String name) {
        String[] subPath = Arrays.copyOf(elements, elements.length + 1);
        subPath[subPath.length - 1] = name;
        return new SubmissionPath(subPath);
    }

    public String getOwnName() {
        return this.elements[this.elements.length - 1];
    }

    public SubmissionPath dropFirstElement() {
        String[] newPath = Arrays.copyOfRange(elements, 1, elements.length);
        return new SubmissionPath(newPath);
    }
}
