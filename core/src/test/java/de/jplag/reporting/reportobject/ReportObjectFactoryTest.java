package de.jplag.reporting.reportobject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.TestBase;
import de.jplag.exceptions.ExitException;
import de.jplag.reporting.reportobject.model.Version;

class ReportObjectFactoryTest extends TestBase {
    private static final String BASECODE = "basecode";
    private static final String BASECODE_BASE = "basecode-base";

    @Test
    void testVersionLoading() {
        Assertions.assertNotNull(ReportObjectFactory.REPORT_VIEWER_VERSION);
        Assertions.assertNotEquals(Version.DEVELOPMENT, ReportObjectFactory.REPORT_VIEWER_VERSION);
    }

    @Test
    void testCreateAndSaveReportWithBasecode() throws ExitException, IOException {
        JPlagResult result = runJPlag(BASECODE, it -> it.withBaseCodeSubmissionDirectory(new File(BASE_PATH, BASECODE_BASE)));
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
        int fileSignature = 0;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            fileSignature = randomAccessFile.readInt();
        }
        return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
    }

}