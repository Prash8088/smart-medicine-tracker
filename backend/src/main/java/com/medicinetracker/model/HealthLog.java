package com.medicinetracker.model;

public class HealthLog {
    private String id;
    private String uid;
    private String date;            // ISO date string
    private String time;            // HH:mm
    private Double bloodPressureSystolic;
    private Double bloodPressureDiastolic;
    private Double heartRate;
    private Double bloodSugar;
    private Double weight;
    private Double temperature;
    private String notes;
    private String createdAt;       // ISO datetime

    public HealthLog() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public Double getBloodPressureSystolic() { return bloodPressureSystolic; }
    public void setBloodPressureSystolic(Double bloodPressureSystolic) { this.bloodPressureSystolic = bloodPressureSystolic; }

    public Double getBloodPressureDiastolic() { return bloodPressureDiastolic; }
    public void setBloodPressureDiastolic(Double bloodPressureDiastolic) { this.bloodPressureDiastolic = bloodPressureDiastolic; }

    public Double getHeartRate() { return heartRate; }
    public void setHeartRate(Double heartRate) { this.heartRate = heartRate; }

    public Double getBloodSugar() { return bloodSugar; }
    public void setBloodSugar(Double bloodSugar) { this.bloodSugar = bloodSugar; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
