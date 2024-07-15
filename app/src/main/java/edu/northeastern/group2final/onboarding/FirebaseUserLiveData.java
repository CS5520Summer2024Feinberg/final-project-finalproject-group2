package edu.northeastern.group2final.onboarding;

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


    // set the value of this object to the value of the current FirebaseUser
    //  when this object is created.
    private AuthStateListener authStateListener =
            firebaseAuth -> setValue(firebaseAuth.getCurrentUser());

    // When this object has active observers, start observing the FirebaseAuth state to see if
    //  there is currently a logged in user.
    @Override
    protected void onActive() {
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    // When this object no longer has active observers, stop observing the FirebaseAuth state to
    //  prevent memory leaks.
    @Override
    protected void onInactive() {
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
