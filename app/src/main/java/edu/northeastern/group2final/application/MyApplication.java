package edu.northeastern.group2final.application;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseFirestore.getInstance();
        FirebaseAuth.getInstance();
    }
}