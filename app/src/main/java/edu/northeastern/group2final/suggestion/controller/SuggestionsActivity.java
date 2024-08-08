package edu.northeastern.group2final.suggestion.controller;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import edu.northeastern.group2final.R;
import edu.northeastern.group2final.photoSharing.view.PhotoSharingActivity;
import edu.northeastern.group2final.suggestion.model.LLMResponse;
import edu.northeastern.group2final.suggestion.model.Suggestion;
import edu.northeastern.group2final.suggestion.view.LLMViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.kienht.bubblepicker.BubblePickerListener;
import com.kienht.bubblepicker.adapter.BubblePickerAdapter;
import com.kienht.bubblepicker.model.PickerItem;
import com.kienht.bubblepicker.rendering.BubblePicker;

public class SuggestionsActivity extends AppCompatActivity {

    private LLMViewModel viewModel;
    TextView textView;
    BubblePicker bubblePicker;
    List<Suggestion> suggestions;
    TextView blockingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggestions);
        bubblePicker = findViewById(R.id.picker);
        textView = findViewById(R.id.textView);
        viewModel = new ViewModelProvider(this).get(LLMViewModel.class);

        viewModel.getResponseLiveData().observe(this, llmResponse -> {
            if (llmResponse != null && llmResponse.getChoices() != null && !llmResponse.getChoices().isEmpty()) {
                LLMResponse.Choice firstChoice = llmResponse.getChoices().get(0);
                if (firstChoice != null && firstChoice.getMessage() != null) {
                    suggestions = parseSuggestions(firstChoice.getMessage().getContent());
                    System.out.println(suggestions.size());
                    textView.setText(firstChoice.getMessage().getContent());
                    presentBubbles();
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
                String prompt = part.substring(0, idx).trim();
                String content = part.substring(idx + 3).trim();
                suggestions.add(new Suggestion(prompt, content));
                System.out.println("Prompt: " + prompt + ", Content: " + content);
            }
        }
        return suggestions;
    }

    private void presentBubbles() {
        if (bubblePicker == null || suggestions == null || suggestions.isEmpty()) {
            return; // Exit if bubblePicker is not initialized or there are no suggestions
        }

        // Create a list of PickerItems for the BubblePicker
        ArrayList<PickerItem> pickerItems = new ArrayList<>();
        Random random = new Random();

        for (Suggestion suggestion : suggestions) {
            PickerItem item = new PickerItem();
            item.setTitle(suggestion.getPrompt());
            item.setTextColor(Color.BLACK);
//            item.setCustomData(suggestion);
//            item.setColor(random.nextInt());
            item.setColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))); // Generate random color
//            item.setImgDrawable(getDrawable(R.drawable.morning));
            pickerItems.add(item);
        }

        // Ensure BubblePicker is initialized
        bubblePicker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return pickerItems.size();
            }

            @Override
            public PickerItem getItem(int position) {
                return pickerItems.get(position);
            }
        });

        // Set the listener for BubblePicker
        bubblePicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(PickerItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
//                Suggestion suggestion = (Suggestion) item.getCustomData();
                showBlockingView();
                new Handler().postDelayed(() -> {
                    removeBlockingView();
                    bubblePicker.setEnabled(true); // Re-enable the BubblePicker after 5 seconds
                }, 5000);

                Intent intent = new Intent(SuggestionsActivity.this, PhotoSharingActivity.class);
                startActivity(intent);
            }

            @Override
            public void onBubbleDeselected(PickerItem item) {
                // Handle the event when a bubble is deselected
            }
        });
    }

    private void showBlockingView() {
        // Create a blocking view and add it to the main layout
        blockingView = new TextView(this);
        blockingView.setText("Interaction Disabled");
        blockingView.setTextSize(24);
        blockingView.setBackgroundColor(Color.argb(150, 0, 0, 0)); // Semi-transparent black
        blockingView.setTextColor(Color.WHITE);
        blockingView.setGravity(android.view.Gravity.CENTER);

        // Set layout parameters to cover the whole screen
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        );

        // Add the blocking view to the root layout
        ((android.widget.FrameLayout) findViewById(android.R.id.content)).addView(blockingView, params);
    }

    private void removeBlockingView() {
        // Remove the blocking view from the root layout
        if (blockingView != null) {
            ((android.widget.FrameLayout) findViewById(android.R.id.content)).removeView(blockingView);
            blockingView = null;
        }
    }

}