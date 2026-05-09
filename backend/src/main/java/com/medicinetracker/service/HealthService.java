package com.medicinetracker.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicinetracker.model.HealthLog;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class HealthService {

    private static final String COLLECTION = "healthLogs";

    private Firestore db() {
        return FirestoreClient.getFirestore();
    }

    public HealthLog create(String uid, HealthLog log) throws ExecutionException, InterruptedException {
        log.setUid(uid);
        log.setCreatedAt(Instant.now().toString());
        DocumentReference ref = db().collection(COLLECTION).document();
        log.setId(ref.getId());
        ref.set(toMap(log)).get();
        return log;
    }

    public List<HealthLog> listByUid(String uid) throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = db().collection(COLLECTION)
                .whereEqualTo("uid", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().get();
        List<HealthLog> list = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            list.add(fromDoc(doc));
        }
        return list;
    }

    public long countAll() throws ExecutionException, InterruptedException {
        return db().collection(COLLECTION).get().get().size();
    }

    private HealthLog fromDoc(DocumentSnapshot doc) {
        HealthLog h = new HealthLog();
        h.setId(doc.getId());
        h.setUid(doc.getString("uid"));
        h.setDate(doc.getString("date"));
        h.setTime(doc.getString("time"));
        h.setBloodPressureSystolic(doc.getDouble("bloodPressureSystolic"));
        h.setBloodPressureDiastolic(doc.getDouble("bloodPressureDiastolic"));
        h.setHeartRate(doc.getDouble("heartRate"));
        h.setBloodSugar(doc.getDouble("bloodSugar"));
        h.setWeight(doc.getDouble("weight"));
        h.setTemperature(doc.getDouble("temperature"));
        h.setNotes(doc.getString("notes"));
        h.setCreatedAt(doc.getString("createdAt"));
        return h;
    }

    private Map<String, Object> toMap(HealthLog h) {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", h.getUid());
        map.put("date", h.getDate());
        map.put("time", h.getTime());
        map.put("bloodPressureSystolic", h.getBloodPressureSystolic());
        map.put("bloodPressureDiastolic", h.getBloodPressureDiastolic());
        map.put("heartRate", h.getHeartRate());
        map.put("bloodSugar", h.getBloodSugar());
        map.put("weight", h.getWeight());
        map.put("temperature", h.getTemperature());
        map.put("notes", h.getNotes());
        map.put("createdAt", h.getCreatedAt());
        return map;
    }
}
