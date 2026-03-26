package org.trs.therepairsystem.service.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.trs.therepairsystem.common.enums.StorageProvider;
import org.trs.therepairsystem.common.exception.BusinessException;
import org.trs.therepairsystem.config.AttachmentStorageProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class LocalAttachmentStorageService implements AttachmentStorageService {

    private final AttachmentStorageProperties storageProperties;

    @Override
    public StorageProvider provider() {
        return StorageProvider.LOCAL;
    }

    @Override
    public void upload(String objectKey, MultipartFile file) {
        Path fullPath = resolveAndCheckPath(objectKey);
        try {
            Files.createDirectories(fullPath.getParent());
            file.transferTo(fullPath);
        } catch (IOException ex) {
            throw new BusinessException("附件保存失败", ex);
        }
    }

    @Override
    public byte[] download(String objectKey) {
        Path fullPath = resolveAndCheckPath(objectKey);
        if (!Files.exists(fullPath)) {
            throw new BusinessException("附件文件不存在");
        }
        try {
            return Files.readAllBytes(fullPath);
        } catch (IOException ex) {
            throw new BusinessException("读取附件文件失败", ex);
        }
    }

    @Override
    public void delete(String objectKey) {
        Path fullPath = resolveAndCheckPath(objectKey);
        try {
            Files.deleteIfExists(fullPath);
        } catch (IOException ex) {
            throw new BusinessException("删除附件文件失败", ex);
        }
    }

    private Path resolveAndCheckPath(String objectKey) {
        Path root = Paths.get(storageProperties.getBasePath()).toAbsolutePath().normalize();
        Path target = root.resolve(objectKey).normalize();
        if (!target.startsWith(root)) {
            throw new BusinessException("非法文件路径");
        }
        return target;
    }
}
