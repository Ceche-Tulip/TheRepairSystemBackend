package org.trs.therepairsystem.service.storage;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.trs.therepairsystem.common.enums.StorageProvider;
import org.trs.therepairsystem.common.exception.BusinessException;
import org.trs.therepairsystem.config.AttachmentStorageProperties;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioAttachmentStorageService implements AttachmentStorageService {

    private final AttachmentStorageProperties storageProperties;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        AttachmentStorageProperties.Minio minio = storageProperties.getMinio();
        if (!minio.isEnabled()) {
            return;
        }
        if (isBlank(minio.getEndpoint()) || isBlank(minio.getAccessKey()) || isBlank(minio.getSecretKey())) {
            throw new BusinessException("MinIO 已启用，但 endpoint/accessKey/secretKey 未完整配置");
        }

        this.minioClient = MinioClient.builder()
                .endpoint(minio.getEndpoint())
                .credentials(minio.getAccessKey(), minio.getSecretKey())
                .build();

        if (minio.isAutoCreateBucket()) {
            ensureBucketExists(minio.getBucket());
        }
    }

    @Override
    public StorageProvider provider() {
        return StorageProvider.MINIO;
    }

    @Override
    public void upload(String objectKey, MultipartFile file) {
        MinioClient client = requireClient();
        String bucket = storageProperties.getMinio().getBucket();
        try (InputStream inputStream = file.getInputStream()) {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception ex) {
            throw new BusinessException("附件上传到 MinIO 失败", ex);
        }
    }

    @Override
    public byte[] download(String objectKey) {
        MinioClient client = requireClient();
        String bucket = storageProperties.getMinio().getBucket();
        try (InputStream inputStream = client.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectKey)
                .build())) {
            return inputStream.readAllBytes();
        } catch (ErrorResponseException ex) {
            if (ex.errorResponse() != null && "NoSuchKey".equalsIgnoreCase(ex.errorResponse().code())) {
                throw new BusinessException("附件文件不存在");
            }
            throw new BusinessException("从 MinIO 读取附件失败", ex);
        } catch (Exception ex) {
            throw new BusinessException("从 MinIO 读取附件失败", ex);
        }
    }

    @Override
    public void delete(String objectKey) {
        MinioClient client = requireClient();
        String bucket = storageProperties.getMinio().getBucket();
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .build());
        } catch (Exception ex) {
            throw new BusinessException("删除 MinIO 附件失败", ex);
        }
    }

    private void ensureBucketExists(String bucket) {
        MinioClient client = requireClient();
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception ex) {
            throw new BusinessException("初始化 MinIO bucket 失败", ex);
        }
    }

    private MinioClient requireClient() {
        if (minioClient == null) {
            throw new BusinessException("MinIO 存储未启用");
        }
        return minioClient;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
