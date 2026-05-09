package com.medicinetracker.controller;

import com.medicinetracker.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Set or revoke admin custom claim on a Firebase user.
     * Body: { "email": "user@example.com", "admin": true/false }
     */
    @PostMapping("/set-claim")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setClaim(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        boolean isAdmin = Boolean.TRUE.equals(body.get("admin"));
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("{\"error\":\"email is required\"}");
        }
        try {
            adminService.setAdminClaim(email, isAdmin);
            return ResponseEntity.ok(Map.of("message", "Claim updated for " + email));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to set claim: " + e.getMessage()));
        }
    }

    /**
     * List all user profiles from Firestore users collection.
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listUsers() {
        try {
            List<Map<String, Object>> users = adminService.listUsers();
            return ResponseEntity.ok(users);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("{\"error\":\"Failed to list users\"}");
        }
    }

    /**
     * Return aggregated analytics: users count, medicines count, health logs count.
     */
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAnalytics() {
        try {
            Map<String, Object> stats = adminService.getAnalytics();
            return ResponseEntity.ok(stats);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("{\"error\":\"Failed to get analytics\"}");
        }
    }
}
