package de.jplag.reporting.reportobject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReportObjectFactoryTest {
    @Test
    void testVersion() {
        Assertions.assertNotNull(ReportObjectFactory.REPORT_VIEWER_VERSION);
    }
}