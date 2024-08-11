package edu.northeastern.group2final.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import edu.northeastern.group2final.entity.User;

public class FirestoreRepository {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public FirestoreRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public Task<Void> createUserDocument(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", user.getUid());
        userMap.put("displayName", user.getDisplayName());
        userMap.put("email", user.getEmail());

        return db.collection("users").document(user.getUid()).set(userMap)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Current user name:" + user.getDisplayName()))
                .addOnFailureListener(e -> Log.e("Firestore", "Error creating user document", e));

    }

    public Task<DocumentSnapshot> getUserDocument(String uid) {
        return db.collection("users").document(uid).get();
    }

    public Task<Void> updateUserDocument(User user) {
        return db.collection("users").document(user.getUid()).set(user, SetOptions.merge());
    }
}
