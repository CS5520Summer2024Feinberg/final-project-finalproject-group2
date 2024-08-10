package edu.northeastern.group2final.suggestion.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group2final.R;
import edu.northeastern.group2final.overview.OverviewActivity;
import edu.northeastern.group2final.onboarding.controller.MainActivity;
import edu.northeastern.group2final.suggestion.model.LLMResponse;
import edu.northeastern.group2final.suggestion.model.Suggestion;
import edu.northeastern.group2final.suggestion.view.LLMViewModel;


public class SuggestionsActivity extends AppCompatActivity {

    private LLMViewModel viewModel;

    TextView textView;
    TextView s1TitleTextView;
    TextView s2TitleTextView;
    TextView s3TitleTextView;

    Button s1ContentButton;
    Button s2ContentButton;
    Button s3ContentButton;
    List<Suggestion> suggestions;
    TextView blockingView;
    ImageView detailsIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggestions);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        textView = findViewById(R.id.textView);
        s1TitleTextView = findViewById(R.id.s1_title);
        s2TitleTextView = findViewById(R.id.s2_title);
        s3TitleTextView = findViewById(R.id.s3_title);

        s1ContentButton = findViewById(R.id.s1_content);
        s2ContentButton = findViewById(R.id.s2_content);
        s3ContentButton = findViewById(R.id.s3_content);

        detailsIv = findViewById(R.id.details);
        detailsIv.setOnClickListener(v ->
                startActivity(new Intent(SuggestionsActivity.this, OverviewActivity.class)));

        viewModel = new ViewModelProvider(this).get(LLMViewModel.class);

        viewModel.getResponseLiveData().observe(this, llmResponse -> {
            if (llmResponse != null && llmResponse.getChoices() != null && !llmResponse.getChoices().isEmpty()) {
                LLMResponse.Choice firstChoice = llmResponse.getChoices().get(0);
                if (firstChoice != null && firstChoice.getMessage() != null) {
                    suggestions = parseSuggestions(firstChoice.getMessage().getContent());
                    textView.setText("Pick a Suggestion!");
                    presentSuggestions();
                } else {
                    textView.setText("No content available");
                }
            } else {
                textView.setText("Response not valid or empty");
            }
        });

        viewModel.sendRequestToOpenAI("I have procrastination in the morning when I wake up; recommend me 3 good/fun activities that I can finish in 10 mins at home as my morning routine to share in social media. All suggestions should be in the form of ''short summary - detail''");
    }

    private List<Suggestion> parseSuggestions(String suggestionsText) {
        System.out.println("calling parseSuggestions !!!" + suggestionsText);

        suggestionsText = suggestionsText.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();

        // Split the suggestions text into parts using a regular expression that matches one or more newlines
        String[] parts = suggestionsText.split("\\n{2,}");
        System.out.println("Number of parts: " + parts.length);

        List<Suggestion> suggestions = new ArrayList<>();
        for (String part : parts) {
            System.out.println("Processing part: " + part);
            int idx = part.indexOf("-");
            if (idx != -1) {
                String prompt = part.substring(2, idx).trim();
                String content = part.substring(idx + 2).trim();
                suggestions.add(new Suggestion(prompt, content));
                System.out.println("Prompt: " + prompt + ", Content: " + content);
            }
        }
        return suggestions;
    }

    private void presentSuggestions() {
        if (suggestions == null || suggestions.size() != 3) {
            return;
        }
        s1TitleTextView.setText(suggestions.get(0).getPrompt());
        s2TitleTextView.setText(suggestions.get(1).getPrompt());
        s3TitleTextView.setText(suggestions.get(2).getPrompt());
        s1ContentButton.setText(getTruncatedContent(suggestions.get(0).getContent()));
        s2ContentButton.setText(getTruncatedContent(suggestions.get(1).getContent()));
        s3ContentButton.setText(getTruncatedContent(suggestions.get(2).getContent()));
    }

    private String getTruncatedContent(String content) {
        if (content.length() > 20) {
            return content.substring(0, 40) + "...";
        } else {
            return content;
        }
    }


    private void removeBlockingView() {
        // Remove the blocking view from the root layout
        if (blockingView != null) {
            ((android.widget.FrameLayout) findViewById(android.R.id.content)).removeView(blockingView);
            blockingView = null;
        }
    }

    public void executeSuggestionOne(View view) {
        // Get the button text
        if (suggestions == null || suggestions.size() != 3) return;

        String buttonText = suggestions.get(0).getContent();

        saveSuggestionToDB(0);
        // Create a full-screen TextView
        showBlocking(buttonText);

    }

    public void executeSuggestionTwo(View view) {
        // Get the button text
        if (suggestions == null || suggestions.size() != 3) return;

        String buttonText = suggestions.get(1).getContent();

        saveSuggestionToDB(1);

        // Create a full-screen TextView
        showBlocking(buttonText);

    }

    public void executeSuggestionThree(View view) {
        // Get the button text
        if (suggestions == null || suggestions.size() != 3) return;

        String buttonText = suggestions.get(2).getContent();

        saveSuggestionToDB(2);

        // Create a full-screen TextView
        showBlocking(buttonText);
    }

    private void showBlocking(String buttonText) {
        blockingView = new TextView(this);
        blockingView.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        ));
        blockingView.setBackgroundColor(Color.parseColor("#AA000000")); // Semi-transparent black background
        blockingView.setText(buttonText);
        blockingView.setTextColor(Color.WHITE);
        blockingView.setTextSize(30);
        blockingView.setGravity(android.view.Gravity.CENTER);

        // Add the view to the root layout
        ((android.widget.FrameLayout) findViewById(android.R.id.content)).addView(blockingView);

        // Remove the view after 10 seconds
        new Handler().postDelayed(this::removeBlockingView, 10000);
    }

    private void saveSuggestionToDB(int index) {
        if (suggestions == null || suggestions.size() < 1) return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.d("SuggestionsActivity", "User not logged in.");
            return;
        }

        Suggestion selectedSuggestion = suggestions.get(index);
        Suggestion suggestionToSave = new Suggestion(
                currentUser.getUid(),
                selectedSuggestion.getPrompt(),
                selectedSuggestion.getContent()
        );
        viewModel.saveSuggestion(suggestionToSave);
        Log.d("SuggestionsActivity", "Suggestion saved to database.");
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showLogoutConfirmationDialog();
    }
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Perform logout
                    logout();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Dismiss the dialog and continue
                    dialog.dismiss();
                })
                .show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}