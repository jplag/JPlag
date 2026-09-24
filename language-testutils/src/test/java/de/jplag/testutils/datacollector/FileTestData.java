package de.jplag.testutils.datacollector;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.util.FileUtils;

/**
 * Provides test source from a file.
 */
class FileTestData implements TestData {
    private final File file;

    FileTestData(File file) {
        this.file = file;
    }

    @Override
    public List<Token> parseTokens(Language language) throws ParsingException {
        return language.parse(Set.of(file), false);
    }

    @Override
    public String[] getSourceLines() throws IOException {
        return FileUtils.readFileContent(this.file).lines().toArray(String[]::new);
    }

    @Override
    public String describeTestSource() {
        return "(File: " + this.file.getName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileTestData that = (FileTestData) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }

    @Override
    public String toString() {
        return this.file.getName();
    }
}
