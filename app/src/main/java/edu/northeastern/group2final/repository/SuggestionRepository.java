package edu.northeastern.group2final.repository;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.northeastern.group2final.suggestion.model.Suggestion;

public class SuggestionRepository {
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

    public void getSuggestionsForPastWeek(String userId, OnSuggestionsLoadedListener listener) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        Date oneWeekAgo = cal.getTime();

        db.collection("suggestions")
                .whereEqualTo("userId", userId)
                .whereGreaterThan("createdAt", oneWeekAgo)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Suggestion> suggestions = queryDocumentSnapshots.toObjects(Suggestion.class);
                    listener.onSuggestionsLoaded(suggestions);
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                });

        Log.d("SuggestionRepository", "getSuggestionsForPastWeek called");
    }

    public void getSuggestionsForUser(String userId, OnSuggestionsLoadedListener listener) {
        db.collection("suggestions")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Suggestion> suggestions = queryDocumentSnapshots.toObjects(Suggestion.class);
                    listener.onSuggestionsLoaded(suggestions);
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                });
    }

    public interface OnSuggestionsLoadedListener {
        void onSuggestionsLoaded(List<Suggestion> suggestions);
        void onError(String errorMessage);
    }
}