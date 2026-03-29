package org.trs.therepairsystem.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentDispositionUtilsTest {

    @Test
    void shouldBuildAsciiSafeHeaderForChineseName() {
        String header = ContentDispositionUtils.buildAttachmentHeaderValue("维修图.jpg");

        assertTrue(header.startsWith("attachment; filename=\""));
        assertTrue(header.contains("filename*=UTF-8''"));
        assertTrue(header.contains("%E7%BB%B4%E4%BF%AE%E5%9B%BE.jpg"));
        assertFalse(header.contains("维修图"));
    }

    @Test
    void shouldFallbackWhenOriginalNameIsBlank() {
        String header = ContentDispositionUtils.buildAttachmentHeaderValue("   ");
        assertTrue(header.contains("filename=\"file\""));
    }
}
