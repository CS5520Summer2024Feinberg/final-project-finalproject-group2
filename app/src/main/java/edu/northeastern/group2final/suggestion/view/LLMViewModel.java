package edu.northeastern.group2final.suggestion.view;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group2final.application.GetUpApplication;
import edu.northeastern.group2final.repository.SuggestionRepository;
import edu.northeastern.group2final.suggestion.model.ChatRequest;
import edu.northeastern.group2final.suggestion.model.LLMResponse;
import edu.northeastern.group2final.suggestion.model.Message;
import edu.northeastern.group2final.suggestion.model.Suggestion;
import edu.northeastern.group2final.suggestion.serviceapi.LLMApiService;
import edu.northeastern.group2final.suggestion.serviceapi.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LLMViewModel extends ViewModel {
    private static final String TAG = "LLMViewModel";
    private MutableLiveData<LLMResponse> responseLiveData = new MutableLiveData<>();
    private SuggestionRepository suggestionRepository;

    public LLMViewModel() {
        GetUpApplication app = GetUpApplication.getInstance();
        suggestionRepository = app.getSuggestionRepository();
    }


    public Task<Void> saveSuggestion(Suggestion suggestion) {
        return suggestionRepository.saveSuggestion(suggestion)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Suggestion saved successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving suggestion", e));
    }

    public Task<List<Suggestion>> getSuggestionsForPastWeek(String userId) {
        return suggestionRepository.getSuggestionsForPastWeek(userId)
                .addOnSuccessListener(suggestions -> Log.d(TAG, "Retrieved " + suggestions.size() + " suggestions for past week"))
                .addOnFailureListener(e -> Log.e(TAG, "Error getting suggestions for past week", e));
    }

    public Task<List<Suggestion>> getMostRecentSuggestions(String userId) {
        return suggestionRepository.getMostRecentSuggestions(userId)
                .addOnSuccessListener(suggestions -> Log.d(TAG, "Retrieved " + suggestions.size() + " most recent suggestions"))
                .addOnFailureListener(e -> Log.e(TAG, "Error getting most recent suggestions", e));
    }

    public Task<List<Suggestion>> getAllSuggestionsForUser(String userId) {
        return suggestionRepository.getAllSuggestionsForUser(userId)
                .addOnSuccessListener(suggestions -> Log.d(TAG, "Retrieved " + suggestions.size() + " suggestions for user"))
                .addOnFailureListener(e -> Log.e(TAG, "Error getting all suggestions for user", e));
    }

    // Method to send all suggestions to OpenAI
    public void sendAllSuggestionsToOpenAI(String userId, String additionalPrompt) {
        suggestionRepository.getAllSuggestionsForUser(userId)
                .addOnSuccessListener(suggestions -> {
                    String suggestionsString = suggestionsToString(suggestions);
                    String prompt = "Based on all these suggestions:\n" + suggestionsString +
                            "\n" + additionalPrompt;
                    Log.i(TAG, "Prompt: " + prompt);
                    //sendRequestToOpenAI(prompt);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error getting all suggestions", e));
    }


    private String suggestionsToString(List<Suggestion> suggestions) {
        StringBuilder sb = new StringBuilder();
        for (Suggestion suggestion : suggestions) {
            sb.append("- ").append(suggestion.getPrompt()).append(": ").append(suggestion.getContent()).append("\n");
        }
        return sb.toString();
    }

    public LiveData<LLMResponse> getResponseLiveData() {
        return responseLiveData;
    }

    public void sendRequestToOpenAI(String userInput) {
        ChatRequest request = createChatRequest(userInput);
        LLMApiService apiService = RetrofitClient.getInstance().getApiService();

        apiService.getResponse(request).enqueue(new Callback<LLMResponse>() {
            @Override
            public void onResponse(Call<LLMResponse> call, Response<LLMResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getChoices().isEmpty()) {
                    responseLiveData.postValue(response.body());
                } else {
                    responseLiveData.postValue(new LLMResponse(false, "Failed to get a valid response"));
                }
            }

            @Override
            public void onFailure(Call<LLMResponse> call, Throwable t) {
                responseLiveData.postValue(new LLMResponse(false, "API call failed: " + t.getMessage()));
            }
        });

    }


    private ChatRequest createChatRequest(String content) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("user", content));

        ChatRequest request = new ChatRequest();
        request.setModel("gpt-3.5-turbo");
        request.setMessages(messages);
        return request;
    }
}