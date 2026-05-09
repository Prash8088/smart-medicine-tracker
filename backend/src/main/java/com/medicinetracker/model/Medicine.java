package com.medicinetracker.model;

import java.util.List;

public class Medicine {
    private String id;
    private String uid;
    private String name;
    private String dosage;
    private String frequency;       // e.g. "daily", "twice_daily"
    private List<String> times;     // e.g. ["08:00", "20:00"]
    private String startDate;       // ISO date string
    private String endDate;         // ISO date string (nullable)
    private boolean active;
    private String notes;
    private String lastSentAt;      // ISO datetime, used to avoid duplicate email reminders

    public Medicine() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public List<String> getTimes() { return times; }
    public void setTimes(List<String> times) { this.times = times; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getLastSentAt() { return lastSentAt; }
    public void setLastSentAt(String lastSentAt) { this.lastSentAt = lastSentAt; }
}
