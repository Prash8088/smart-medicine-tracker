package com.medicinetracker.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicinetracker.model.Medicine;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class MedicineService {

    private static final String COLLECTION = "medicines";

    private Firestore db() {
        return FirestoreClient.getFirestore();
    }

    public Medicine create(String uid, Medicine medicine) throws ExecutionException, InterruptedException {
        medicine.setUid(uid);
        medicine.setActive(true);
        DocumentReference ref = db().collection(COLLECTION).document();
        medicine.setId(ref.getId());
        Map<String, Object> data = toMap(medicine);
        ref.set(data).get();
        return medicine;
    }

    public List<Medicine> listByUid(String uid) throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = db().collection(COLLECTION)
                .whereEqualTo("uid", uid)
                .get().get();
        List<Medicine> list = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            list.add(fromDoc(doc));
        }
        return list;
    }

    public Medicine getById(String uid, String medicineId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db().collection(COLLECTION).document(medicineId).get().get();
        if (!doc.exists()) return null;
        Medicine m = fromDoc(doc);
        if (!uid.equals(m.getUid())) return null; // enforce ownership
        return m;
    }

    public Medicine update(String uid, String medicineId, Medicine updates)
            throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db().collection(COLLECTION).document(medicineId).get().get();
        if (!doc.exists()) return null;
        Medicine existing = fromDoc(doc);
        if (!uid.equals(existing.getUid())) return null;

        updates.setId(medicineId);
        updates.setUid(uid);
        db().collection(COLLECTION).document(medicineId).set(toMap(updates)).get();
        return updates;
    }

    public boolean delete(String uid, String medicineId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db().collection(COLLECTION).document(medicineId).get().get();
        if (!doc.exists()) return false;
        Medicine existing = fromDoc(doc);
        if (!uid.equals(existing.getUid())) return false;
        db().collection(COLLECTION).document(medicineId).delete().get();
        return true;
    }

    public List<Medicine> getAllActive() throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = db().collection(COLLECTION)
                .whereEqualTo("active", true)
                .get().get();
        List<Medicine> list = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            list.add(fromDoc(doc));
        }
        return list;
    }

    public void updateLastSentAt(String medicineId, String isoTimestamp)
            throws ExecutionException, InterruptedException {
        db().collection(COLLECTION).document(medicineId)
                .update("lastSentAt", isoTimestamp).get();
    }

    public long countAll() throws ExecutionException, InterruptedException {
        return db().collection(COLLECTION).get().get().size();
    }

    @SuppressWarnings("unchecked")
    private Medicine fromDoc(DocumentSnapshot doc) {
        Medicine m = new Medicine();
        m.setId(doc.getId());
        m.setUid(doc.getString("uid"));
        m.setName(doc.getString("name"));
        m.setDosage(doc.getString("dosage"));
        m.setFrequency(doc.getString("frequency"));
        m.setTimes((List<String>) doc.get("times"));
        m.setStartDate(doc.getString("startDate"));
        m.setEndDate(doc.getString("endDate"));
        Boolean active = doc.getBoolean("active");
        m.setActive(active != null && active);
        m.setNotes(doc.getString("notes"));
        m.setLastSentAt(doc.getString("lastSentAt"));
        return m;
    }

    private Map<String, Object> toMap(Medicine m) {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", m.getUid());
        map.put("name", m.getName());
        map.put("dosage", m.getDosage());
        map.put("frequency", m.getFrequency());
        map.put("times", m.getTimes() != null ? m.getTimes() : Collections.emptyList());
        map.put("startDate", m.getStartDate());
        map.put("endDate", m.getEndDate());
        map.put("active", m.isActive());
        map.put("notes", m.getNotes());
        map.put("lastSentAt", m.getLastSentAt());
        return map;
    }
}
