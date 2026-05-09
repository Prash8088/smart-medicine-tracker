package com.medicinetracker.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.medicinetracker.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class AdminService {

    @Autowired
    private UserService userService;

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private HealthService healthService;

    public void setAdminClaim(String email, boolean isAdmin) throws Exception {
        UserRecord user = FirebaseAuth.getInstance().getUserByEmail(email);
        Map<String, Object> claims = new HashMap<>();
        claims.put("admin", isAdmin);
        FirebaseAuth.getInstance().setCustomUserClaims(user.getUid(), claims);
    }

    public List<Map<String, Object>> listUsers() throws ExecutionException, InterruptedException {
        return userService.listAll();
    }

    public Map<String, Object> getAnalytics() throws ExecutionException, InterruptedException {
        Map<String, Object> stats = new HashMap<>();
        stats.put("usersCount", userService.countAll());
        stats.put("medicinesCount", medicineService.countAll());
        stats.put("healthLogsCount", healthService.countAll());
        return stats;
    }
}
