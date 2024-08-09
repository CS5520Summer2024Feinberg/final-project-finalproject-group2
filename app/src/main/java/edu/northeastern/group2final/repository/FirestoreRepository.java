package edu.northeastern.group2final.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreRepository {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public FirestoreRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public Task<Void> createUserDocument(FirebaseUser user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", user.getEmail());
        userMap.put("displayName", user.getDisplayName());

        Log.d("Firestore", "Attempting to create/update user doc for UID:" + user.getUid());

        return db.collection("users").document(user.getUid()).set(userMap)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User document successfully created/updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error creating user document", e));

    }

    public Task<DocumentSnapshot> getUserDocument() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            return db.collection("users").document(currentUser.getUid()).get();
        }
        return null;
    }
}
