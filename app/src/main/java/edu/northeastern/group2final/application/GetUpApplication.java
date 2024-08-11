package edu.northeastern.group2final.application;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.northeastern.group2final.repository.FirestoreRepository;
import edu.northeastern.group2final.repository.SuggestionRepository;
import edu.northeastern.group2final.repository.UserRepository;

public class GetUpApplication extends Application {

    private static GetUpApplication instance;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private UserRepository userRepository;
    private SuggestionRepository suggestionRepository;
    private FirestoreRepository firestoreRepository;

    public static GetUpApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        userRepository = new UserRepository(firestore);
        suggestionRepository = new SuggestionRepository(firestore);
        firestoreRepository = new FirestoreRepository();
    }
    public FirebaseFirestore getFirestore() {
        return firestore;
    }
    public FirebaseAuth getFirebaseAuth() { return firebaseAuth; }
    public UserRepository getUserRepository() {
        return userRepository;
    }
    public SuggestionRepository getSuggestionRepository() {
        return suggestionRepository;
    }
    public FirestoreRepository getFirestoreRepository() {
        return firestoreRepository;
    }
}