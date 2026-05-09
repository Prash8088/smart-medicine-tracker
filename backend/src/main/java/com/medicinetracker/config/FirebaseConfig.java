package com.medicinetracker.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials.path:}")
    private String credentialsPath;

    @Value("${firebase.project.id:}")
    private String projectId;

    @Value("${admin.bootstrap.email:}")
    private String bootstrapAdminEmail;

    @PostConstruct
    public void initFirebase() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                GoogleCredentials credentials;
                if (StringUtils.hasText(credentialsPath)) {
                    try (InputStream serviceAccount = new FileInputStream(credentialsPath)) {
                        credentials = GoogleCredentials.fromStream(serviceAccount);
                    }
                } else {
                    // Fall back to application default credentials (e.g., GCP environment)
                    credentials = GoogleCredentials.getApplicationDefault();
                }

                FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                        .setCredentials(credentials);

                if (StringUtils.hasText(projectId)) {
                    optionsBuilder.setProjectId(projectId);
                }

                FirebaseApp.initializeApp(optionsBuilder.build());
                log.info("Firebase initialized successfully");
            } catch (IOException e) {
                log.error("Failed to initialize Firebase: {}", e.getMessage());
            }
        }

        bootstrapAdmin();
    }

    private void bootstrapAdmin() {
        if (!StringUtils.hasText(bootstrapAdminEmail)) {
            log.info("BOOTSTRAP_ADMIN_EMAIL not set; skipping admin bootstrap");
            return;
        }

        try {
            UserRecord user = FirebaseAuth.getInstance().getUserByEmail(bootstrapAdminEmail);
            Map<String, Object> claims = new HashMap<>();
            claims.put("admin", true);
            FirebaseAuth.getInstance().setCustomUserClaims(user.getUid(), claims);
            log.info("Admin bootstrap succeeded for email: {}", bootstrapAdminEmail);
        } catch (Exception e) {
            log.warn("Admin bootstrap failed for email '{}': {}", bootstrapAdminEmail, e.getMessage());
        }
    }
}
