package com.medicinetracker.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.medicinetracker.model.Medicine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailReminderService {

    private static final Logger log = LoggerFactory.getLogger(EmailReminderService.class);

    @Autowired
    private MedicineService medicineService;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${reminder.from.email:}")
    private String fromEmail;

    /**
     * Runs every minute. Checks all active medicines and sends an email reminder
     * if the current HH:mm matches one of the medicine's scheduled times
     * and no email has been sent for this minute yet.
     */
    @Scheduled(fixedDelay = 60000)
    public void sendReminders() {
        if (mailSender == null) {
            log.debug("Mail sender not configured; skipping reminder job");
            return;
        }

        String currentMinute = DateTimeFormatter.ofPattern("HH:mm")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());

        try {
            List<Medicine> medicines = medicineService.getAllActive();
            for (Medicine medicine : medicines) {
                processMedicine(medicine, currentMinute);
            }
        } catch (Exception e) {
            log.error("Error in email reminder job: {}", e.getMessage(), e);
        }
    }

    private void processMedicine(Medicine medicine, String currentMinute)
            throws Exception {

        if (medicine.getTimes() == null || medicine.getTimes().isEmpty()) {
            return;
        }

        boolean isDue = medicine.getTimes().stream()
                .anyMatch(t -> currentMinute.equals(t));

        if (!isDue) return;

        // Check if we already sent a reminder this minute
        String lastSentAt = medicine.getLastSentAt();
        if (StringUtils.hasText(lastSentAt)) {
            String lastSentMinute = lastSentAt.length() >= 16
                    ? lastSentAt.substring(11, 16)   // extract HH:mm from ISO string
                    : "";
            String today = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now());
            String lastSentDate = lastSentAt.length() >= 10 ? lastSentAt.substring(0, 10) : "";
            if (currentMinute.equals(lastSentMinute) && today.equals(lastSentDate)) {
                return; // already sent this minute
            }
        }

        String userEmail = getUserEmail(medicine.getUid());
        if (!StringUtils.hasText(userEmail)) {
            log.warn("No email found for uid {}; skipping reminder for medicine {}",
                    medicine.getUid(), medicine.getName());
            return;
        }

        sendEmail(userEmail, medicine, currentMinute);

        medicineService.updateLastSentAt(medicine.getId(), Instant.now().toString());
    }

    private String getUserEmail(String uid) {
        try {
            UserRecord user = FirebaseAuth.getInstance().getUser(uid);
            return user.getEmail();
        } catch (Exception e) {
            log.warn("Could not retrieve email for uid {}: {}", uid, e.getMessage());
            return null;
        }
    }

    private void sendEmail(String to, Medicine medicine, String currentMinute) {
        try {
            String effectiveFrom = StringUtils.hasText(fromEmail) ? fromEmail : to;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(effectiveFrom);
            message.setTo(to);
            message.setSubject("Medicine Reminder: " + medicine.getName());
            message.setText(
                    "Hello,\n\n" +
                    "This is a reminder to take your medicine:\n\n" +
                    "  Medicine: " + medicine.getName() + "\n" +
                    "  Dosage:   " + medicine.getDosage() + "\n" +
                    "  Time:     " + currentMinute + " UTC\n\n" +
                    (medicine.getNotes() != null ? "  Notes: " + medicine.getNotes() + "\n\n" : "") +
                    "Stay healthy!\n" +
                    "— Smart Medicine Tracker"
            );
            mailSender.send(message);
            log.info("Reminder email sent to {} for medicine '{}'", to, medicine.getName());
        } catch (Exception e) {
            log.error("Failed to send reminder email to {} for medicine '{}': {}",
                    to, medicine.getName(), e.getMessage());
        }
    }
}
