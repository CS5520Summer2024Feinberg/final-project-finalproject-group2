package edu.northeastern.group2final;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.northeastern.group2final.databinding.ActivitySuggestionBinding;

public class SuggestionActivity extends AppCompatActivity {
    private static final int SIGN_IN_RESULT_CODE = 666;
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

        binding.logOutBtn.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_RESULT_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == Activity.RESULT_OK) {
                // User is authenticated
                String loggedInMessage = "Welcome Back " +
                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                Snackbar.make(binding.main, loggedInMessage,
                                Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }
}