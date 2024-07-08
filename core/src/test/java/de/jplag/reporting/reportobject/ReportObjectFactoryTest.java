package de.jplag.reporting.reportobject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.TestBase;
import de.jplag.exceptions.ExitException;
import de.jplag.reporting.reportobject.model.Version;

import static org.junit.jupiter.api.Assertions.*;

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
        File testZip = File.createTempFile("result", ".zip");

        ReportObjectFactory reportObjectFactory = new ReportObjectFactory(testZip);
        reportObjectFactory.createAndSaveReport(result);

        assertNotNull(result);
        assertTrue(isArchive(testZip));
    }

    @Test
    void testWithSameNameSubmissions() throws ExitException, IOException {
        File submission1 = new File(BASE_PATH, "basecode/A");
        File submission2 = new File(BASE_PATH, "basecode/B");
        File submission3 = new File(BASE_PATH, "basecode-sameNameOfSubdirectoryAndRootdirectory/A");
        File submission4 = new File(BASE_PATH, "basecode-sameNameOfSubdirectoryAndRootdirectory/B");
        List<String> submissions = Stream.of(submission1, submission2, submission3, submission4).map(File::toString).toList();
        JPlagResult result = runJPlag(submissions, it -> it.withBaseCodeSubmissionDirectory(new File(BASE_PATH, BASECODE_BASE)));
        File testZip = File.createTempFile("result", ".zip");

        ReportObjectFactory reportObjectFactory = new ReportObjectFactory(testZip);
        reportObjectFactory.createAndSaveReport(result);

        assertNotNull(result);
        assertTrue(isArchive(testZip));
    }

    /**
     * Checks if the given file is a valid archive
     *
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