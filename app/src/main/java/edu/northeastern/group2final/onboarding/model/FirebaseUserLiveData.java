package edu.northeastern.group2final.onboarding.model;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;

/**
 * This class observes the current FirebaseUser.
 *  If there is no logged in user, FirebaseUser will be null.
 */
public class FirebaseUserLiveData extends LiveData<FirebaseUser> {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private AuthStateListener authStateListener =
            firebaseAuth -> setValue(firebaseAuth.getCurrentUser());
    @Override
    protected void onActive() {
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    @Override
    protected void onInactive() {
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
