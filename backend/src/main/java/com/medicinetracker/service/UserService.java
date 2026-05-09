package com.medicinetracker.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicinetracker.model.UserProfile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private static final String COLLECTION = "users";

    private Firestore db() {
        return FirestoreClient.getFirestore();
    }

    public UserProfile createOrUpdate(String uid, UserProfile profile)
            throws ExecutionException, InterruptedException {
        profile.setUid(uid);
        profile.setUpdatedAt(Instant.now().toString());

        DocumentReference ref = db().collection(COLLECTION).document(uid);
        DocumentSnapshot existing = ref.get().get();

        if (!existing.exists()) {
            profile.setCreatedAt(Instant.now().toString());
        } else {
            String createdAt = existing.getString("createdAt");
            profile.setCreatedAt(createdAt != null ? createdAt : Instant.now().toString());
        }

        ref.set(toMap(profile)).get();
        return profile;
    }

    public UserProfile getByUid(String uid) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db().collection(COLLECTION).document(uid).get().get();
        if (!doc.exists()) return null;
        return fromDoc(doc);
    }

    public List<Map<String, Object>> listAll() throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = db().collection(COLLECTION).get().get();
        List<Map<String, Object>> list = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            list.add(doc.getData());
        }
        return list;
    }

    public long countAll() throws ExecutionException, InterruptedException {
        return db().collection(COLLECTION).get().get().size();
    }

    private UserProfile fromDoc(DocumentSnapshot doc) {
        UserProfile p = new UserProfile();
        p.setUid(doc.getId());
        p.setEmail(doc.getString("email"));
        p.setDisplayName(doc.getString("displayName"));
        p.setPhoneNumber(doc.getString("phoneNumber"));
        p.setDateOfBirth(doc.getString("dateOfBirth"));
        p.setGender(doc.getString("gender"));
        p.setEmergencyContact(doc.getString("emergencyContact"));
        p.setCreatedAt(doc.getString("createdAt"));
        p.setUpdatedAt(doc.getString("updatedAt"));
        return p;
    }

    private Map<String, Object> toMap(UserProfile p) {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", p.getUid());
        map.put("email", p.getEmail());
        map.put("displayName", p.getDisplayName());
        map.put("phoneNumber", p.getPhoneNumber());
        map.put("dateOfBirth", p.getDateOfBirth());
        map.put("gender", p.getGender());
        map.put("emergencyContact", p.getEmergencyContact());
        map.put("createdAt", p.getCreatedAt());
        map.put("updatedAt", p.getUpdatedAt());
        return map;
    }
}
