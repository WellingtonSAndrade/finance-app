package com.pagar.finance_api.api.controllers;

import com.pagar.finance_api.core.services.OcrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<String> insertFromUpload(@RequestParam MultipartFile file) {
        String response = ocrService.createTask(file);
        return ResponseEntity.status(201).body(response);
    }

}
