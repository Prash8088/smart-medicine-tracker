package com.medicinetracker.controller;

import com.medicinetracker.model.Medicine;
import com.medicinetracker.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @PostMapping
    public ResponseEntity<?> create(Authentication auth,
                                    @RequestBody Medicine medicine) {
        try {
            String uid = (String) auth.getPrincipal();
            Medicine created = medicineService.create(uid, medicine);
            return ResponseEntity.ok(created);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("{\"error\":\"Failed to create medicine\"}");
        }
    }

    @GetMapping
    public ResponseEntity<?> list(Authentication auth) {
        try {
            String uid = (String) auth.getPrincipal();
            List<Medicine> medicines = medicineService.listByUid(uid);
            return ResponseEntity.ok(medicines);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("{\"error\":\"Failed to list medicines\"}");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(Authentication auth, @PathVariable String id) {
        try {
            String uid = (String) auth.getPrincipal();
            Medicine medicine = medicineService.getById(uid, id);
            if (medicine == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(medicine);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("{\"error\":\"Failed to get medicine\"}");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Authentication auth,
                                    @PathVariable String id,
                                    @RequestBody Medicine medicine) {
        try {
            String uid = (String) auth.getPrincipal();
            Medicine updated = medicineService.update(uid, id, medicine);
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(updated);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("{\"error\":\"Failed to update medicine\"}");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Authentication auth, @PathVariable String id) {
        try {
            String uid = (String) auth.getPrincipal();
            boolean deleted = medicineService.delete(uid, id);
            if (!deleted) return ResponseEntity.notFound().build();
            return ResponseEntity.ok("{\"message\":\"Deleted\"}");
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("{\"error\":\"Failed to delete medicine\"}");
        }
    }
}
