package edu.northeastern.group2final.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.northeastern.group2final.model.ChatRequest;
import edu.northeastern.group2final.model.LLMResponse;
import edu.northeastern.group2final.model.Message;
import edu.northeastern.group2final.serviceapi.LLMApiService;
import edu.northeastern.group2final.serviceapi.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import java.util.ArrayList;
import java.util.List;

public class LLMViewModel extends ViewModel {
    private MutableLiveData<LLMResponse> responseLiveData = new MutableLiveData<>();

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
