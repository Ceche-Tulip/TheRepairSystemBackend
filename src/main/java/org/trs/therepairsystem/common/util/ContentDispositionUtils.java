package org.trs.therepairsystem.common.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class ContentDispositionUtils {

    private static final String DEFAULT_FILENAME = "file";
    private static final int FALLBACK_MAX_LENGTH = 120;

    private ContentDispositionUtils() {
    }

    public static String buildAttachmentHeaderValue(String originalFileName) {
        String safeFileName = (originalFileName == null || originalFileName.isBlank())
                ? DEFAULT_FILENAME
                : originalFileName;

        String fallback = buildAsciiFallbackName(safeFileName);
        String encoded = URLEncoder.encode(safeFileName, StandardCharsets.UTF_8).replace("+", "%20");
        return "attachment; filename=\"" + fallback + "\"; filename*=UTF-8''" + encoded;
    }

    private static String buildAsciiFallbackName(String fileName) {
        String sanitized = fileName
                .replace('\\', '_')
                .replace('/', '_')
                .replace('"', '_')
                .replace(';', '_')
                .replace('\r', '_')
                .replace('\n', '_');

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sanitized.length(); i++) {
            char c = sanitized.charAt(i);
            if (c >= 32 && c <= 126) {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }

        String fallback = sb.toString().trim();
        if (fallback.isBlank()) {
            return DEFAULT_FILENAME;
        }
        if (fallback.length() > FALLBACK_MAX_LENGTH) {
            return fallback.substring(0, FALLBACK_MAX_LENGTH);
        }
        return fallback;
    }
}
