package edu.northeastern.group2final.repository;

import com.google.firebase.firestore.FirebaseFirestore;

import edu.northeastern.group2final.entity.User;

public class UserRepository {
    private FirebaseFirestore db;

    public UserRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void getUserData(String uid, OnUserDataLoadedListener listener) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        listener.onUserDataLoaded(user);
                    } else {
                        listener.onError("User not found");
                    }
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    public interface OnUserDataLoadedListener {
        void onUserDataLoaded(User user);
        void onError(String errorMessage);
    }
}