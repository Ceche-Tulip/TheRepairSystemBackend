package org.trs.therepairsystem.service.storage;

import org.springframework.web.multipart.MultipartFile;
import org.trs.therepairsystem.common.enums.StorageProvider;

public interface AttachmentStorageService {

    StorageProvider provider();

    void upload(String objectKey, MultipartFile file);

    byte[] download(String objectKey);

    void delete(String objectKey);
}
