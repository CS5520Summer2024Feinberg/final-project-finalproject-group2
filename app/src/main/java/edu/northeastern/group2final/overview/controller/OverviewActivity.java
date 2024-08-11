package edu.northeastern.group2final.overview.controller;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import edu.northeastern.group2final.application.GetUpApplication;
import edu.northeastern.group2final.databinding.ActivityOverviewBinding;
import edu.northeastern.group2final.overview.SuggestionAdapter;
import edu.northeastern.group2final.overview.viewmodel.OverviewViewModel;

public class OverviewActivity extends AppCompatActivity {
    private SuggestionAdapter adapter;
    private OverviewViewModel viewModel;
    private ActivityOverviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOverviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GetUpApplication app = (GetUpApplication) getApplication();

        viewModel = new ViewModelProvider(this).get(OverviewViewModel.class);

        setupRecyclerView();
        setupObservers();

        viewModel.loadUserData();
        viewModel.loadSuggestions();
    }

    private void setupRecyclerView() {
        int numberOfColumns = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(numberOfColumns, StaggeredGridLayoutManager.VERTICAL);
        binding.suggestionsRecyclerView.setLayoutManager(layoutManager);

        adapter = new SuggestionAdapter(new ArrayList<>());
        binding.suggestionsRecyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                binding.userDisplayName.setText(user.getDisplayName());
            }
        });

        viewModel.getSuggestionsLiveData().observe(this, suggestions -> {
            if (suggestions != null) {
                adapter.setSuggestions(suggestions);
                adapter.notifyDataSetChanged();
            }
        });

        viewModel.getErrorLiveData().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
