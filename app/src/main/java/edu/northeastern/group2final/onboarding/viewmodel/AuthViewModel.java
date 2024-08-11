package edu.northeastern.group2final.onboarding.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

import edu.northeastern.group2final.application.GetUpApplication;
import edu.northeastern.group2final.onboarding.model.FirebaseUserLiveData;
import edu.northeastern.group2final.onboarding.util.AuthenticationState;
import edu.northeastern.group2final.onboarding.util.SignUpType;
import edu.northeastern.group2final.repository.FirestoreRepository;

public class LoginViewModel extends ViewModel {
    private FirestoreRepository firestoreRepository;
    private FirebaseAuth auth;
    private FirebaseUserLiveData firebaseUserLiveData = new FirebaseUserLiveData();
    private MutableLiveData<String> loginResultLiveData = new MutableLiveData<>();
    private MutableLiveData<AuthenticationState> signUpResultLiveData = new MutableLiveData<>();
    private LiveData<AuthenticationState> authenticationState  =
            Transformations.map(
                    firebaseUserLiveData, user -> {
                        if (user != null) return AuthenticationState.AUTHENTICATED;
                        else return AuthenticationState.UNAUTHENTICATED;
                    });

    public LoginViewModel() {
        GetUpApplication app = GetUpApplication.getInstance();
        this.firestoreRepository = app.getFirestoreRepository();
        this.auth = app.getFirebaseAuth();
    }

    public LiveData<AuthenticationState> getAuthenticationStateLiveData() {
        return authenticationState;
    }
    public LiveData<String> getLoginResultLiveData() {
        return loginResultLiveData;
    }
    public LiveData<AuthenticationState> getSignUpResultLiveData() {
        return signUpResultLiveData;
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
                                        loginResultLiveData.setValue("Login Successful");
                                    })
                                    .addOnFailureListener(e -> {
                                        loginResultLiveData.setValue("Failed to create user document");
                                    });
                        } else {
                            loginResultLiveData.setValue("User is null after successful login");
                        }
                    }
                    else {
                        loginResultLiveData.setValue("Login failed.");
                    }
                });
    }

    public void signUp(IdpResponse response) {
        if (response == null) {
            signUpResultLiveData.setValue("Sign-up canceled");
            return;
        }

        if (response.isSuccessful()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                createUserDocument(user);
            } else {
                signUpResultLiveData.setValue("Sign-up successful, but user is null");
            }
        } else {
            signUpResultLiveData.setValue("Sign-up failed: " + response.getError().getMessage());
        }
    }

    private void createUserDocument(FirebaseUser user) {
        firestoreRepository.createUserDocument(user)
                .addOnSuccessListener(aVoid -> {
                    String message = "Authentication successful";
                    loginResultLiveData.setValue(message);
                    signUpResultLiveData.setValue(message);
                })
                .addOnFailureListener(e -> {
                    String message = "Failed to create user document: " + e.getMessage();
                    loginResultLiveData.setValue(message);
                    signUpResultLiveData.setValue(message);
                });
    }

    public List<AuthUI.IdpConfig> getProviders(SignUpType signUpType) {
        switch(signUpType) {
            case EMAIL:
                return Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build());
            case GOOGLE:
                return Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build());
            default:
                throw new IllegalArgumentException("Invalid sign-up type");
        }
    }

    public String getGreetingMessage() {
        FirebaseUser user = new FirebaseUserLiveData().getValue();
        if (user != null && user.getDisplayName() != null) {
            return "Hello " + user.getDisplayName();
        } else {
            return "Hello User";
        }
    }
}
