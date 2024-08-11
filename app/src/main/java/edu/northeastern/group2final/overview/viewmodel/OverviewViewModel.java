package edu.northeastern.group2final.overview.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import edu.northeastern.group2final.application.GetUpApplication;
import edu.northeastern.group2final.entity.User;
import edu.northeastern.group2final.repository.SuggestionRepository;
import edu.northeastern.group2final.repository.UserRepository;
import edu.northeastern.group2final.suggestion.model.Suggestion;

public class OverviewViewModel extends ViewModel {
    private static final String TAG = "OverviewViewModel";
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Suggestion>> suggestionsLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private UserRepository userRepository;
    private SuggestionRepository suggestionRepository;
    private FirebaseAuth auth;

    public OverviewViewModel() {
        GetUpApplication app = GetUpApplication.getInstance();
        userRepository = app.getUserRepository();
        suggestionRepository = app.getSuggestionRepository();
        auth = app.getFirebaseAuth();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<List<Suggestion>> getSuggestionsLiveData() {
        return suggestionsLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            errorLiveData.setValue("No user logged in");
            return;
        }
        String uid = currentUser.getUid();
        userRepository.getUserData(uid)
                .addOnSuccessListener(user -> {
                    userLiveData.setValue(user);
                    Log.d(TAG, "User data loaded successfully");
                })
                .addOnFailureListener(e -> {
                    errorLiveData.setValue("Failed to load user data: " + e.getMessage());
                    Log.e(TAG, "Error loading user data", e);
                });
    }

    public void loadSuggestions() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            errorLiveData.setValue("No user logged in");
            return;
        }

        String uid = currentUser.getUid();
        suggestionRepository.getAllSuggestionsForUser(uid)
                .addOnSuccessListener(suggestions -> {
                    suggestionsLiveData.setValue(suggestions);
                    Log.d(TAG, "Suggestions loaded successfully. Count: " + suggestions.size());
                })
                .addOnFailureListener(e -> {
                    errorLiveData.setValue("Failed to load suggestions: " + e.getMessage());
                    Log.e(TAG, "Error loading suggestions", e);
                });
    }

    public void loadAllData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            errorLiveData.setValue("No user logged in");
            return;
        }

        String uid = currentUser.getUid();
        Task<User> userTask = userRepository.getUserData(uid);
        Task<List<Suggestion>> suggestionsTask = suggestionRepository.getAllSuggestionsForUser(uid);

        Tasks.whenAllSuccess(userTask, suggestionsTask)
                .addOnSuccessListener(results -> {
                    User user = (User) results.get(0);
                    List<Suggestion> suggestions = (List<Suggestion>) results.get(1);
                    userLiveData.setValue(user);
                    suggestionsLiveData.setValue(suggestions);
                    Log.d(TAG, "All data loaded successfully");
                })
                .addOnFailureListener(e -> {
                    errorLiveData.setValue("Failed to load data: " + e.getMessage());
                    Log.e(TAG, "Error loading all data", e);
                });
    }
}