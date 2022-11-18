package de.jplag.reporting.jsonfactory;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.reporting.jsonfactory.DirectoryManager;

public class DirectoryManagerTest {

    @Test
    public void TestCreateDirectoryWithBasecode() throws IOException {
        String path = "src\\test\\resources\\de\\jplag\\samples\\output\\submissions";
        String name = "A";
        File file = new File("src\\test\\resources\\de\\jplag\\samples\\basecode\\A\\TerrainType.java");
        File directory = DirectoryManager.createDirectory(path, name, file);
        Assertions.assertNotNull(directory);
        Assertions.assertEquals(directory,
                new File("src\\test\\resources\\de\\jplag\\samples\\output\\submissions\\A\\TerrainType.java"));
    }

    @Test
    public void TestCreateDirectoryWithFilesAsSubmissions() throws IOException {
        String path = "src\\test\\resources\\de\\jplag\\samples\\output\\submissions";
        String name = "Submission1.java";
        File file = new File("src\\test\\resources\\de\\jplag\\samples\\FilesAsSubmissions\\Submission1.java");
        File directory = DirectoryManager.createDirectory(path, name, file);
        Assertions.assertNotNull(directory);
        Assertions.assertEquals(directory,
                new File("src\\test\\resources\\de\\jplag\\samples\\output\\submissions\\Submission1.java\\Submission1.java"));
    }

    @Test
    public void TestCreateDirectoryWithOtherSeparator() throws IOException {
        String path = "src/test/resources/de/jplag/samples/output/submissions";
        String name = "Submission1.java";
        File file = new File("src/test/resources/de/jplag/samples/FilesAsSubmissions/Submission1.java");
        File directory = DirectoryManager.createDirectory(path, name, file);
        Assertions.assertNotNull(directory);
        Assertions.assertEquals(directory,
                new File("src/test/resources/de/jplag/samples/output/submissions/Submission1.java/Submission1.java"));
    }

    @Test
    public void TestCreateDirectoryAndFilenameContainsSubfoldername() throws IOException {
        String path = "src\\test\\resources\\de\\jplag\\samples\\output\\submissions";
        String name = "A";
        File file = new File("src\\test\\resources\\de\\jplag\\samples\\basecode\\A\\ABCDEA.java");
        File directory = DirectoryManager.createDirectory(path, name, file);
        Assertions.assertNotNull(directory);
        Assertions.assertEquals(directory, new File("src\\test\\resources\\de\\jplag\\samples\\output\\submissions\\A\\ABCDEA.java"));
    }

    @Test
    public void TestCreateDirectoryWithMultipleFolders() throws IOException {
        String path = "src\\test\\resources\\de\\jplag\\samples\\output\\submissions";
        String name = "A";
        File file = new File("src\\test\\resources\\de\\jplag\\samples\\basecode\\A\\B\\A\\ABCDEA.java");
        File directory = DirectoryManager.createDirectory(path, name, file);
        Assertions.assertNotNull(directory);
        Assertions.assertEquals(directory,
                new File("src\\test\\resources\\de\\jplag\\samples\\output\\submissions\\A\\B\\A\\ABCDEA.java"));
    }
}
