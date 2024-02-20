package de.jplag.reporting;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FilePathUtilTest {
    private static final String JOINED = "left/right";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";

    @Test
    void testJoinPath() {
        assertEquals(JOINED, FilePathUtil.joinZipPathSegments(LEFT, RIGHT));
    }

    @Test
    void testJoinPathWithLeftSlashSuffix() {
        assertEquals(JOINED, FilePathUtil.joinZipPathSegments(LEFT + "/", RIGHT));
    }

    @Test
    void testJoinPathWithRightSlashSuffix() {
        assertEquals(JOINED, FilePathUtil.joinZipPathSegments(LEFT, "/" + RIGHT));
    }
}