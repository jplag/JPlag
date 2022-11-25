package de.jplag.reporting.jsonfactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.TestBase;

class DirectoryManagerTest extends TestBase {
    final String rootDirA = "A";
    final String fileNameSubmission1AsRootDir = "Submission1.java";
    final String emptySubDir = "";
    final String multiSubDirBA = Path.of("B", "A").toString();
    final String fileNameTerrainType = "TerrainType.java";
    final String fileNameSubmission1 = "Submission1.java";
    final String path = Path.of(BASE_PATH, "output", "submissions").toString();
    final String basecode = "basecode";
    final String FilesAsSubmissions = "FilesAsSubmissions";
    final String basecodeWithSameNameOfSubdirectoryAndRootdirectory = "basecode-sameNameOfSubdirectoryAndRootdirectory";

    String[] submissionDirectory = {basecode, FilesAsSubmissions, basecodeWithSameNameOfSubdirectoryAndRootdirectory};
    String[] names = {rootDirA, fileNameSubmission1AsRootDir, rootDirA};
    String[] subDirs = {emptySubDir, emptySubDir, multiSubDirBA};
    String[] fileNames = {fileNameTerrainType, fileNameSubmission1, fileNameTerrainType};
    File[] files = {new File(Path.of(BASE_PATH, submissionDirectory[0], names[0], subDirs[0], fileNames[0]).toString()),
            new File(Path.of(BASE_PATH, submissionDirectory[1], subDirs[1], fileNames[1]).toString()),
            new File(Path.of(BASE_PATH, submissionDirectory[2], names[2], subDirs[2], fileNames[2]).toString())};
    File[] submissionRoots = new File[files.length];
    File[] expectedFiles = new File[files.length];

    @Test
    void testCreateDirectoryWithTestCases() throws IOException {
        for (int index = 0; index < files.length; index++) {
            expectedFiles[index] = new File(Path.of(path, names[index], subDirs[index], fileNames[index]).toString());
            submissionRoots[index] = new File(Path.of(BASE_PATH, submissionDirectory[index], names[index]).toString());
        }
        testCreateDirectory(names, files, expectedFiles, submissionRoots);
    }

    void testCreateDirectory(String[] names, File[] files, File[] expectedFiles, File[] submissionRoots) throws IOException {
        int length = files.length;
        for (int testCaseIndex = 0; testCaseIndex < length; testCaseIndex++) {
            File directory = DirectoryManager.createDirectory(path, names[testCaseIndex], files[testCaseIndex], submissionRoots[testCaseIndex]);
            Assertions.assertNotNull(directory);
            Assertions.assertEquals(expectedFiles[testCaseIndex].getPath(), directory.getPath());
        }
    }
}
