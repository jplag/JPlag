package de.jplag.reporting.jsonfactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import de.jplag.reporting.reportobject.writer.DummyResultWriter;
import de.jplag.util.RelativePath;

class TestableReportWriter extends DummyResultWriter {

    /**
     * Stores JSON content objects keyed by their file path.
     */
    public final Map<RelativePath, Object> jsonEntries;

    public TestableReportWriter() {
        jsonEntries = new HashMap<>();
    }

    @Override
    public void addJsonEntry(Object jsonContent, RelativePath path) {
        jsonEntries.put(path, jsonContent);
    }

    public Object getJsonEntry(RelativePath path) {
        return jsonEntries.get(path);
    }
}
