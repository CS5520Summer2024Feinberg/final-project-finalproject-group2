package edu.northeastern.group2final.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.northeastern.group2final.entity.User;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private FirebaseFirestore db;

    public UserRepository(FirebaseFirestore firestore) {
        this.db = firestore;
    }

    public Task<User> getUserData(String uid) {
        return db.collection("users").document(uid)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            Log.d(TAG, "User data retrieved successfully for UID: " + uid);
                            return user;
                        } else {
                            Log.w(TAG, "No user found for UID: " + uid);
                            throw new Exception("User not found");
                        }
                    } else {
                        Log.e(TAG, "Error getting user data", task.getException());
                        throw task.getException();
                    }
                });
    }
}