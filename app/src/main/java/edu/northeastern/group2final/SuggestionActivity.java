package edu.northeastern.group2final;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.northeastern.group2final.databinding.ActivitySuggestionBinding;

public class SuggestionActivity extends AppCompatActivity {
    private ActivitySuggestionBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuggestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get current user
        firebaseAuth = firebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null & currentUser.getDisplayName() != null)  {
            binding.tv.setText("Hello " + currentUser.getDisplayName());
        } else {
            binding.tv.setText("Hello Anonymous");
        }
    }
}