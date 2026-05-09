package com.medicinetracker.controller;

import com.medicinetracker.model.UserProfile;
import com.medicinetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(Authentication auth) {
        try {
            String uid = (String) auth.getPrincipal();
            UserProfile profile = userService.getByUid(uid);
            if (profile == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(profile);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("{\"error\":\"Failed to get profile\"}");
        }
    }

    @PostMapping("/me")
    public ResponseEntity<?> createOrUpdateProfile(Authentication auth,
                                                    @RequestBody UserProfile profile) {
        try {
            String uid = (String) auth.getPrincipal();
            UserProfile updated = userService.createOrUpdate(uid, profile);
            return ResponseEntity.ok(updated);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("{\"error\":\"Failed to update profile\"}");
        }
    }
}
