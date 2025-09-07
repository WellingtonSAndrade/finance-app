package com.pagar.finance_api.infrastructure.client.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageClient {
    String upload(MultipartFile file);
}
