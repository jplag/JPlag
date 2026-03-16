package de.jplag.java_cpg.ai.variables;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a variable name, potentially with a path (e.g., for namespaced or class-scoped variables). Path is
 * currently not used in equals and hashCode methods.
 * @author ujiqk
 * @version 1.0
 */
public class VariableName {

    private final String localName;
    private final String path;

    /**
     * Creates a new VariableName by splitting the given name into a path and local name.
     * @param name the full variable name, potentially including path segments separated by dots.
     */
    public VariableName(@NotNull String name) {
        String[] names = name.split("\\.");
        if (names.length > 1) {
            this.path = String.join(".", java.util.Arrays.copyOfRange(names, 0, names.length - 1));
            this.localName = names[names.length - 1];
        } else {
            this.path = "";
            this.localName = name;
        }
    }

    /**
     * @return the local name of the variable (without a path).
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * @return the path of the variable.
     */
    public String getPath() {
        return path;
    }

    @Override
    public int hashCode() {
        return localName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        VariableName that = (VariableName) o;
        return localName.equals(that.localName);
    }

    @Override
    public String toString() {
        return "VariableName{" + "localName='" + localName + '\'' + ", path='" + path + '\'' + '}';
    }

}
