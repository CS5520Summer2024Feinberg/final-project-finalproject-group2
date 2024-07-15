package edu.northeastern.group2final.onboarding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {

    // authentication states
   enum AuthenticationState {
        AUTHENTICATED,
        UNAUTHENTICATED,
        INVALID_AUTHENTICATION
    }

    // FirebaseUserLiveData instance
    private FirebaseUserLiveData firebaseUserLiveData = new FirebaseUserLiveData();

    // Create an authenticationState variable based off the FirebaseUserLiveData object
    // Transformed authentication state LiveData
    private LiveData<AuthenticationState> authenticationStateLiveData =
            Transformations.map(firebaseUserLiveData, input -> input != null ? AuthenticationState.AUTHENTICATED : AuthenticationState.UNAUTHENTICATED);

    public LiveData<AuthenticationState> getAuthenticationStateLiveData() {
        return authenticationStateLiveData;
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
