package edu.northeastern.group2final.overview;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import edu.northeastern.group2final.databinding.ActivityOverviewBinding;
import edu.northeastern.group2final.entity.User;
import edu.northeastern.group2final.repository.SuggestionManager;
import edu.northeastern.group2final.repository.UserRepository;
import edu.northeastern.group2final.suggestion.model.Suggestion;
import edu.northeastern.group2final.suggestion.view.LLMViewModel;

public class OverviewActivity extends AppCompatActivity {
    private SuggestionAdapter adapter;
    private LLMViewModel viewModel;
    private SuggestionManager suggestionManager;
    private ActivityOverviewBinding binding;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOverviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LLMViewModel.class);
        suggestionManager = new SuggestionManager(viewModel);
        userRepository = new UserRepository();

        int numberOfColumns = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(numberOfColumns, StaggeredGridLayoutManager.VERTICAL);
        binding.suggestionsRecyclerView.setLayoutManager(layoutManager);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadUserData(currentUser);
        }
    }

    private void loadUserData(FirebaseUser currentUser) {
        String uid = currentUser.getUid();
        userRepository.getUserData(uid, new UserRepository.OnUserDataLoadedListener() {
            @Override
            public void onUserDataLoaded(User user) {
                runOnUiThread(() -> {
                    binding.userDisplayName.setText(user.getDisplayName());
                    loadSuggestions(uid);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(OverviewActivity.this, "Error loading user data: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadSuggestions(String userId) {
        suggestionManager.getAllSuggestionsForUser(userId, new SuggestionManager.OnSuggestionsLoadedListener() {
            @Override
            public void onSuggestionsLoaded(List<Suggestion> suggestions) {
                runOnUiThread(() -> {
                    if (suggestions.isEmpty()) {
                        Toast.makeText(OverviewActivity.this, "No suggestions found", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter = new SuggestionAdapter(suggestions);
                        binding.suggestionsRecyclerView.setAdapter(adapter);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(OverviewActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}