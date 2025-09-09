package de.jplag.reporting.reportobject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.Version;
import de.jplag.exceptions.ExitException;
import de.jplag.java.JavaLanguage;
import de.jplag.options.JPlagOptions;

class ReportObjectFactoryTest {
    protected static final String BASE_PATH = Path.of("..", "core", "src", "test", "resources", "de", "jplag", "samples").toString();
    private static final String BASECODE = "basecode";
    private static final String BASECODE_BASE = "basecode-base";

    @Test
    void testVersionLoading() {
        Assertions.assertNotNull(ReportObjectFactory.REPORT_VIEWER_VERSION);
        Assertions.assertNotEquals(Version.DEVELOPMENT, ReportObjectFactory.REPORT_VIEWER_VERSION);
    }

    @Test
    void testCreateAndSaveReportWithBasecode() throws ExitException, IOException {
        File submissionDir = new File(String.join("/", BASE_PATH) + "/" + BASECODE);
        File basecodeDir = new File(String.join("/", BASE_PATH) + "/" + BASECODE_BASE);
        JPlagOptions options = new JPlagOptions(new JavaLanguage(), Set.of(submissionDir), Set.of()).withBaseCodeSubmissionDirectory(basecodeDir);
        JPlagResult result = JPlag.run(options);
        File testResult = File.createTempFile("result", ".jplag");

        ReportObjectFactory reportObjectFactory = new ReportObjectFactory(testResult);
        reportObjectFactory.createAndSaveReport(result);

        assertNotNull(result);
        assertTrue(isArchive(testResult));
    }

    /**
     * Checks if the given file is a valid archive.
     * @param file The file to check
     * @return True, if file is an archive
     */
    private static boolean isArchive(File file) throws IOException {
        int fileSignature;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            fileSignature = randomAccessFile.readInt();
        }
        return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
    }

}