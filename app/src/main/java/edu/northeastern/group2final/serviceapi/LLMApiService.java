package edu.northeastern.group2final.serviceapi;

import edu.northeastern.group2final.model.ChatRequest;
import edu.northeastern.group2final.model.LLMResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LLMApiService {
    @POST("v1/chat/completions")
    Call<LLMResponse> getResponse(@Body ChatRequest chatRequest);
}