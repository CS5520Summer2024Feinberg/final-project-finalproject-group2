package edu.northeastern.group2final.repository;

import java.util.List;

import edu.northeastern.group2final.suggestion.model.Suggestion;
import edu.northeastern.group2final.suggestion.view.LLMViewModel;

public class SuggestionManager {
    private LLMViewModel viewModel;
    private SuggestionRepository suggestionRepository;

    public SuggestionManager(LLMViewModel viewModel) {
        this.viewModel = viewModel;
        this.suggestionRepository = new SuggestionRepository();
    }
    public void getAllSuggestionsForUser(String userId, OnSuggestionsLoadedListener listener) {
        suggestionRepository.getAllSuggestionsForUser(userId, new SuggestionRepository.OnSuggestionsLoadedListener() {
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

    public void getSelectedSuggestionsForPastWeek(String userId, OnSuggestionsLoadedListener listener) {
        suggestionRepository.getSelectedSuggestionsForPastWeek(userId, new SuggestionRepository.OnSuggestionsLoadedListener() {
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

    public interface OnSuggestionsLoadedListener {
        void onSuggestionsLoaded(List<Suggestion> suggestions);
        void onError(String errorMessage);
    }
}