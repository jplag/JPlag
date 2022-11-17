package de.jplag.reporting.reportobject;

import de.jplag.reporting.reportobject.model.Version;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReportObjectFactoryTest {
    @Test
    void testVersion() {
        Assertions.assertNotNull(ReportObjectFactory.REPORT_VIEWER_VERSION);
        Assertions.assertNotEquals(Version.DEVELOPMENT, ReportObjectFactory.REPORT_VIEWER_VERSION);
    }
}