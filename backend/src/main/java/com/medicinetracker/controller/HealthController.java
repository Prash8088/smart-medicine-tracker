package com.medicinetracker.controller;

import com.medicinetracker.model.HealthLog;
import com.medicinetracker.service.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private HealthService healthService;

    @PostMapping
    public ResponseEntity<?> create(Authentication auth,
                                    @RequestBody HealthLog healthLog) {
        try {
            String uid = (String) auth.getPrincipal();
            HealthLog created = healthService.create(uid, healthLog);
            return ResponseEntity.ok(created);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("{\"error\":\"Failed to log health data\"}");
        }
    }

    @GetMapping
    public ResponseEntity<?> list(Authentication auth) {
        try {
            String uid = (String) auth.getPrincipal();
            List<HealthLog> logs = healthService.listByUid(uid);
            return ResponseEntity.ok(logs);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("{\"error\":\"Failed to fetch health logs\"}");
        }
    }
}
