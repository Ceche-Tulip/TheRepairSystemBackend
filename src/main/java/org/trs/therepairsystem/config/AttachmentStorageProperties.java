package org.trs.therepairsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.trs.therepairsystem.common.enums.StorageProvider;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class AttachmentStorageProperties {

    private StorageProvider storageProvider = StorageProvider.LOCAL;

    private String basePath = "uploads";

    private long maxFileSizeBytes = 10L * 1024L * 1024L;

    private List<String> allowedContentTypes = List.of("image/jpeg", "image/png", "image/webp");

    private Minio minio = new Minio();

    @Data
    public static class Minio {
        private boolean enabled = false;
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucket = "repair-attachments";
        private boolean autoCreateBucket = true;
    }
}
