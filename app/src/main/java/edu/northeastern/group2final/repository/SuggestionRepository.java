package edu.northeastern.group2final.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.northeastern.group2final.suggestion.model.Suggestion;

public class SuggestionRepository {
    private static final String TAG = "SuggestionRepository";
    private FirebaseFirestore db;

    public SuggestionRepository(FirebaseFirestore fireStore) {
        db = fireStore;
    }

    public Task<Void> saveSuggestion(Suggestion suggestion) {
        return db.collection("suggestions")
                .add(suggestion)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        suggestion.setId(task.getResult().getId());
                        Log.d(TAG, "Suggestion saved with ID: " + suggestion.getId());
                    } else {
                        Log.e(TAG, "Error saving suggestion", task.getException());
                    }
                    return task.getResult().set(suggestion);
                });
    }

    //
    public Task<List<Suggestion>> getSuggestionsForPastWeek(String userId) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        Date oneWeekAgo = cal.getTime();
        Log.d(TAG, "One week ago: " + oneWeekAgo);

        return db.collection("suggestions")
                .whereEqualTo("userId", userId)
                .whereGreaterThan("createdAt", oneWeekAgo)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<Suggestion> suggestions = task.getResult().toObjects(Suggestion.class);
                        Log.d(TAG, "Retrieved " + suggestions.size() + " suggestions for past week");
                        return suggestions;
                    } else {
                        Log.e(TAG, "Error getting suggestions for past week", task.getException());
                        throw task.getException();
                    }
                });
    }


    public Task<List<Suggestion>> getAllSuggestionsForUser(String userId) {
        return db.collection("suggestions")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<Suggestion> suggestions = task.getResult().toObjects(Suggestion.class);
                        Log.d(TAG, "Retrieved " + suggestions.size() + " suggestions for user");
                        return suggestions;
                    } else {
                        Log.e(TAG, "Error getting all suggestions for user", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<List<Suggestion>> getMostRecentSuggestions(String userId) {
        return db.collection("suggestions")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<Suggestion> suggestions = task.getResult().toObjects(Suggestion.class);
                        Log.d(TAG, "Retrieved " + suggestions.size() + " most recent suggestions");
                        return suggestions;
                    } else {
                        Log.e(TAG, "Error getting most recent suggestions", task.getException());
                        throw task.getException();
                    }
                });

    }

}