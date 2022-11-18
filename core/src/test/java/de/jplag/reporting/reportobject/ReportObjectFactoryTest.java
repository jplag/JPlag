package de.jplag.reporting.reportobject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.reporting.reportobject.model.Version;

class ReportObjectFactoryTest {
    @Test
    void testVersionLoading() {
        Assertions.assertNotNull(ReportObjectFactory.REPORT_VIEWER_VERSION);
        Assertions.assertNotEquals(Version.DEVELOPMENT, ReportObjectFactory.REPORT_VIEWER_VERSION);
    }
}