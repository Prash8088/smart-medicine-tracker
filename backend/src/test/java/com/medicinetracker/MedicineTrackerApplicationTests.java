package com.medicinetracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "firebase.credentials.path=",
    "firebase.project.id=test-project",
    "admin.bootstrap.email=",
    "spring.mail.host=",
    "spring.mail.port=587",
    "spring.mail.username=",
    "spring.mail.password=",
    "reminder.from.email="
})
class MedicineTrackerApplicationTests {

    @Test
    void contextLoads() {
        // Context loading test – verifies that Spring Boot application
        // context initializes without errors given empty Firebase credentials.
    }
}
