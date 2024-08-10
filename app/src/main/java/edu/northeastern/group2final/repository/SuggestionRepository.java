package edu.northeastern.group2final.repository;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.northeastern.group2final.suggestion.model.Suggestion;

public class SuggestionRepository {
    private static final String TAG = "SuggestionRepository";
    private FirebaseFirestore db;
    public SuggestionRepository() {db = FirebaseFirestore.getInstance();}

    public void saveSuggestion(Suggestion suggestion) {
        db.collection("suggestions")
                .add(suggestion)
                .addOnSuccessListener(documentReference -> {
                    suggestion.setId(documentReference.getId());
                })
                .addOnFailureListener(e -> {});
    }

    public void getSelectedSuggestionsForPastWeek(String userId, OnSuggestionsLoadedListener listener) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        Date oneWeekAgo = cal.getTime();

        db.collection("suggestions")
                .whereEqualTo("userId", userId)
                .whereEqualTo("selected", true)  // Assuming you have a 'selected' field
                .whereGreaterThan("createdAt", oneWeekAgo)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Suggestion> suggestions = queryDocumentSnapshots.toObjects(Suggestion.class);
                    Log.d(TAG, "Loaded " + suggestions.size() + " selected suggestions from the past week");
                    listener.onSuggestionsLoaded(suggestions);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting selected suggestions", e);
                    listener.onError(e.getMessage());
                });
    }

    public void getAllSuggestionsForUser(String userId, OnSuggestionsLoadedListener listener) {
        db.collection("suggestions")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Suggestion> suggestions = queryDocumentSnapshots.toObjects(Suggestion.class);
                    listener.onSuggestionsLoaded(suggestions);
                })
                .addOnFailureListener(e -> {
                    Log.e("SuggestionRepository", e.getMessage());
                    listener.onError(e.getMessage());
                });
    }

    public interface OnSuggestionsLoadedListener {
        void onSuggestionsLoaded(List<Suggestion> suggestions);
        void onError(String errorMessage);
    }
}