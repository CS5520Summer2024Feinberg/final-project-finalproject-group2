package edu.northeastern.group2final.onboarding.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.northeastern.group2final.onboarding.model.FirebaseUserLiveData;
import edu.northeastern.group2final.onboarding.util.AuthenticationState;
import edu.northeastern.group2final.repository.FirestoreRepository;

public class LoginViewModel extends ViewModel {
    private FirestoreRepository firestoreRepository;
    private FirebaseAuth auth;
    private FirebaseUserLiveData firebaseUserLiveData = new FirebaseUserLiveData();
    private LiveData<AuthenticationState> authenticationState  =
            Transformations.map(
                    firebaseUserLiveData, user -> {
                        if (user != null) return AuthenticationState.AUTHENTICATED;
                        else return AuthenticationState.UNAUTHENTICATED;
                    });

    public LoginViewModel() {
        this.firestoreRepository = new FirestoreRepository();
        this.auth = FirebaseAuth.getInstance();
    }

    public LiveData<AuthenticationState> getAuthenticationStateLiveData() {
        return authenticationState;
    }

    public void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            firestoreRepository.createUserDocument(user)
                                    .addOnSuccessListener(aVoid -> {
                                        // save to
                                        Log.d("Firestore", "User document created/updated successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Failed to create/update user document", e);
                                    });
                        } else {
                            Log.e("Firestore", "User is null after successful login");
                        }
                    }
                    else {
                        Log.e("Firestore", "Login failed", task.getException());
                    }
                });
    }

    // Method to get a greeting message
    public String getGreetingMessage() {
        FirebaseUser user = new FirebaseUserLiveData().getValue();
        if (user != null && user.getDisplayName() != null) {
            return "Hello " + user.getDisplayName();
        } else {
            return "Hello User";
        }
    }


}
