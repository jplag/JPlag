package de.jplag.java_cpg.ai;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.sarif.PhysicalLocation;

/**
 * Records visited code lines in source files.
 * @author ujiqk
 * @version 1.0
 */
public class VisitedLinesRecorder {

    private final Map<URI, Set<Integer>> visitedLines;
    private final Map<URI, Set<Integer>> possibleLines;
    private final Map<URI, Set<Integer>> detectedDeadLines;
    private int deadLinesCount = 0;
    private int deadCodeCount = 0;

    /**
     * Creates a new VisitedLinesRecorder.
     */
    public VisitedLinesRecorder() {
        visitedLines = new HashMap<>();
        possibleLines = new HashMap<>();
        detectedDeadLines = new HashMap<>();
    }

    /**
     * @param node record lines visited in the given node
     */
    public void recordLinesVisited(@NotNull Node node) {
        PhysicalLocation location = node.getLocation();
        if (location == null) {
            return;
        }
        URI uri = location.getArtifactLocation().getUri();
        Set<Integer> alreadyVisitedLines = visitedLines.computeIfAbsent(uri, _ -> new HashSet<>());
        // Only line numbers and not full regions are stored
        int startLine = location.getRegion().startLine;
        int endLine = location.getRegion().getEndLine();
        for (int line = startLine; line <= endLine; line++) {
            alreadyVisitedLines.add(line);
        }
        // check if file information is already present
        possibleLines.computeIfAbsent(uri, this::countLinesInFile);
    }

    /**
     * @param node record the first line visited in the given node
     */
    public void recordFirstLineVisited(@Nullable Node node) {
        if (node == null) {
            return;
        }
        PhysicalLocation location = node.getLocation();
        if (location == null) {
            return;
        }
        URI uri = location.getArtifactLocation().getUri();
        Set<Integer> alreadyVisitedLines = visitedLines.computeIfAbsent(uri, _ -> new HashSet<>());
        // Only line numbers and not full regions are stored
        int startLine = location.getRegion().startLine;
        alreadyVisitedLines.add(startLine);
        // check if file information is already present
        possibleLines.computeIfAbsent(uri, this::countLinesInFile);
    }

    /**
     * Records that lines are detected to be dead code.
     * @param uri the file URI
     * @param startLine the start line of the dead code region (inclusive)
     * @param endLine the end line of the dead code region (inclusive)
     */
    public void recordDetectedDeadLines(@NotNull URI uri, int startLine, int endLine) {
        assert startLine <= endLine;
        assert startLine >= 0;
        Set<Integer> deadLines = new HashSet<>();
        for (int line = startLine; line <= endLine; line++) {
            deadLines.add(line);
        }
        Set<Integer> alreadyDeadLines = detectedDeadLines.computeIfAbsent(uri, _ -> new HashSet<>());
        alreadyDeadLines.addAll(deadLines);
        deadLinesCount += (endLine - startLine + 1);
        deadCodeCount++;
    }

    /**
     * Records that lines are detected to be dead code.
     * @param node the node representing the dead code region
     */
    public void recordDetectedDeadLines(Node node) {
        if (node == null)
            return;
        PhysicalLocation location = node.getLocation();
        if (location == null) {
            return;
        }
        URI uri = location.getArtifactLocation().getUri();
        int startLine = location.getRegion().startLine;
        int endLine = location.getRegion().getEndLine();
        recordDetectedDeadLines(uri, startLine, endLine);
    }

    /**
     * @return a map of URIs to sets of line numbers that have not been visited
     */
    @NotNull
    @Pure
    @TestOnly
    public Map<URI, Set<Integer>> getNonVisitedLines() {
        Map<URI, Set<Integer>> nonVisitedLines = new HashMap<>();
        for (Map.Entry<URI, Set<Integer>> entry : possibleLines.entrySet()) {
            URI uri = entry.getKey();
            Set<Integer> possible = entry.getValue();
            Set<Integer> visited = visitedLines.getOrDefault(uri, new HashSet<>());
            Set<Integer> nonVisited = new HashSet<>(possible);
            nonVisited.removeAll(visited);
            nonVisitedLines.put(uri, nonVisited);
        }
        return nonVisitedLines;
    }

    /**
     * Checks if the given lines in the file are completely dead (not visited).
     * @param uri the file URI
     * @param startLine the start line of the region (inclusive)
     * @param endLine the end line of the region (inclusive)
     * @return true if all lines in the given range are not visited, false otherwise
     */
    @Pure
    public boolean checkIfCompletelyDead(@NotNull URI uri, int startLine, int endLine) {
        if (startLine == -1 || endLine == -1) {
            return false;
        }
        assert startLine <= endLine;
        assert startLine >= 0;
        Set<Integer> visited = visitedLines.getOrDefault(uri, new HashSet<>());
        for (int line = startLine; line <= endLine; line++) {
            if (visited.contains(line)) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    private Set<Integer> countLinesInFile(@NotNull URI uri) {
        Set<Integer> lines = new HashSet<>();
        try (var reader = new java.io.BufferedReader(new java.io.FileReader(new java.io.File(uri)))) {
            int lineNumber = 1;
            while (reader.readLine() != null) {
                lines.add(lineNumber++);
            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not read file: " + uri, e);
        }
        return lines;
    }

    /**
     * @return the map of visited lines. For testing purposes only.
     */
    @TestOnly
    public Map<URI, Set<Integer>> getVisitedLines() {
        return visitedLines;
    }

    /**
     * @return the number of lines that have been detected to be dead code.
     */
    @TestOnly
    public int getDeadLinesCount() {
        return deadLinesCount;
    }

    /**
     * @return the number of code regions that have been detected to be dead code.
     */
    @TestOnly
    public int getDeadCodeCount() {
        return deadCodeCount;
    }

}
