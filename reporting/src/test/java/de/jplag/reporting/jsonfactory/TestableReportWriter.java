package de.jplag.reporting.jsonfactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import de.jplag.reporting.reportobject.writer.DummyResultWriter;

class TestableReportWriter extends DummyResultWriter {

    /**
     * Stores JSON content objects keyed by their file path.
     */
    public final Map<Path, Object> jsonEntries;

    public TestableReportWriter() {
        jsonEntries = new HashMap<>();
    }

    @Override
    public void addJsonEntry(Object jsonContent, Path path) {
        jsonEntries.put(path, jsonContent);
    }

    public Object getJsonEntry(Path path) {
        return jsonEntries.get(path);
    }
}
