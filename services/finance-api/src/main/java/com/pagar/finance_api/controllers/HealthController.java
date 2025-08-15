package com.pagar.finance_api.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Tag(name = "Health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "ok");
        return ResponseEntity.ok(status);
    }
}
