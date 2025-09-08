package com.pagar.finance_api.infrastructure.client.storage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface StorageClient {
    String upload(MultipartFile file);
}
