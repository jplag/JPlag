package de.jplag.reporting.reportobject;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.TestBase;
import de.jplag.exceptions.ExitException;
import de.jplag.reporting.reportobject.model.Version;

class ReportObjectFactoryTest extends TestBase {
    @Test
    void testVersionLoading() {
        Assertions.assertNotNull(ReportObjectFactory.REPORT_VIEWER_VERSION);
        Assertions.assertNotEquals(Version.DEVELOPMENT, ReportObjectFactory.REPORT_VIEWER_VERSION);
    }

    @Test
    void testCreateAndSaveReportWithBasecode() throws ExitException {
        JPlagResult result = runJPlag("basecode", it -> it.withBaseCodeSubmissionDirectory(new File(BASE_PATH, "basecode-base")));
        String path = Path.of(BASE_PATH, "output", "submissions").toString();
        ReportObjectFactory reportObjectFactory = new ReportObjectFactory();
        reportObjectFactory.createAndSaveReport(result, path);
        Assertions.assertNotNull(result);
    }

}