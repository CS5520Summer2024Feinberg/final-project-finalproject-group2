package edu.northeastern.group2final.onboarding.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

import edu.northeastern.group2final.application.GetUpApplication;
import edu.northeastern.group2final.entity.User;
import edu.northeastern.group2final.onboarding.util.AuthEvent;
import edu.northeastern.group2final.onboarding.util.SignUpType;
import edu.northeastern.group2final.onboarding.util.SingleLiveEvent;
import edu.northeastern.group2final.repository.FirestoreRepository;

public class AuthViewModel extends ViewModel {
    private FirestoreRepository firestoreRepository;
    private FirebaseAuth auth;
    private MutableLiveData<String> authResultLiveData = new MutableLiveData<>();
    private SingleLiveEvent<AuthEvent> authEvent = new SingleLiveEvent<>();

    public AuthViewModel() {
        GetUpApplication app = GetUpApplication.getInstance();
        this.firestoreRepository = app.getFirestoreRepository();
        this.auth = app.getFirebaseAuth();
    }

    public LiveData<String> getAuthResultLiveData() {
        return authResultLiveData;
    }

    public SingleLiveEvent<AuthEvent> getAuthEvent() {
        return authEvent;
    }

    public void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null)
                            fetchUserDocument(user.getUid());
                        else
                            authResultLiveData.setValue("User is null after successful login");
                    } else
                        authResultLiveData.setValue("Login failed: " + task.getException());
                });
    }

    private void fetchUserDocument(String uid) {
        Log.d("AuthViewModel", "Fetching user document for UID: " + uid);
        firestoreRepository
                .getUserDocument(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null && user.isValid()) {
                            Log.d("AuthViewModel", "User document fetched successfully");
                            String message = "Login successful. Welcome back, " +
                                    (user.getDisplayName() != null ? user.getDisplayName() : "User");
                            authResultLiveData.setValue(message);
                            authEvent.setValue(new AuthEvent(AuthEvent.Type.SIGN_IN_SUCCESS, user.getUid(), user.getDisplayName()));
                        } else {
                            Log.w("AuthViewModel", "User document exists but data is incomplete");
                            authResultLiveData.setValue("Login successful, but user data is incomplete.");
                            updateUserDocument(auth.getCurrentUser());
                        }
                    } else {
                        Log.w("AuthViewModel", "User document not found. Creating new document.");
                        authResultLiveData.setValue("Login successful, but user document not found. Creating new document.");
                        createUserDocument(auth.getCurrentUser());
                    }
                })
                .addOnFailureListener(e -> {
                    authResultLiveData.setValue("Failed to fetch user data: " + e.getMessage());
                });
    }

    private void updateUserDocument(FirebaseUser firebaseUser) {
        User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
        firestoreRepository.updateUserDocument(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AuthViewModel", "User document updated successfully");
                    String message = "Profile updated. Welcome, " +
                            (user.getDisplayName() != null ? user.getDisplayName() : "User");
                    authResultLiveData.setValue(message);
                })
                .addOnFailureListener(e -> {
                    Log.e("AuthViewModel", "Failed to update user document", e);
                    authResultLiveData.setValue("Failed to update profile: " + e.getMessage());
                });
    }

    private void createUserDocument(FirebaseUser user) {
        Log.d("AuthViewModel", "Attempting to create user document for UID: " + user.getUid());
        User newUser = new User(
                user.getUid(),
                user.getEmail(),
                user.getDisplayName());
        firestoreRepository.createUserDocument(newUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AuthViewModel", "User document created successfully");
                    String message = "Sign-up successful. Welcome, " +
                            (user.getDisplayName() != null ? user.getDisplayName() : "User");
                    authResultLiveData.setValue(message);
                    authEvent.setValue(new AuthEvent(AuthEvent.Type.SIGN_UP_SUCCESS, user.getUid(), user.getDisplayName()));
                })
                .addOnFailureListener(e -> {
                    Log.e("AuthViewModel", "Failed to create user document", e);
                    String message = "Failed to create user document: " + e.getMessage();
                    authResultLiveData.setValue(message);
                });
    }

    public void handleSignUpResult(IdpResponse response) {
        if (response == null) {
            Log.d("AuthViewModel", "Sign-up canceled");
            authResultLiveData.setValue("Sign-up canceled");
            return;
        }

        if (response.isSuccessful()) {
            Log.d("AuthViewModel", "Sign-up successful");
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                createUserDocument(firebaseUser);
            } else {
                Log.e("AuthViewModel", "Sign-up successful, but FirebaseUser is null");
                authResultLiveData.setValue("Sign-up successful, but user is null");
            }
        } else {
            Log.e("AuthViewModel", "Sign-up failed: " + response.getError().getMessage());
            authResultLiveData.setValue("Sign-up failed: " + response.getError().getMessage());
        }
    }

    public List<AuthUI.IdpConfig> getProviders(SignUpType signUpType) {
        switch (signUpType) {
            case EMAIL:
                return Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build());
            case GOOGLE:
                return Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build());
            default:
                throw new IllegalArgumentException("Invalid sign-up type");
        }
    }
}
