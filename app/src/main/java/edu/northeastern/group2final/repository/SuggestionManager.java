package edu.northeastern.group2final.repository;

import java.util.List;

import edu.northeastern.group2final.suggestion.model.Suggestion;
import edu.northeastern.group2final.suggestion.view.LLMViewModel;

public class SuggestionManager {
    private LLMViewModel viewModel;

    public SuggestionManager(LLMViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void getPastWeekSuggestions(String userId, OnPastWeekSuggestionsLoadedListener listener) {
        viewModel.getSuggestionsForPastWeek(userId, new SuggestionRepository.OnSuggestionsLoadedListener() {
            @Override
            public void onSuggestionsLoaded(List<Suggestion> suggestions) {
                listener.onSuggestionsLoaded(suggestions);
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(errorMessage);
            }
        });
    }

    public interface OnPastWeekSuggestionsLoadedListener {
        void onSuggestionsLoaded(List<Suggestion> suggestions);
        void onError(String errorMessage);
    }
}