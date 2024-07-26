package edu.northeastern.group2final.model;

import java.util.List;

public class LLMResponse {
    private List<Choice> choices;
    private boolean success;  // Indicates if the API call was successful
    private String errorMessage;  // Holds error message in case of failure

    // Default constructor for successful API responses
    public LLMResponse(List<Choice> choices) {
        this.choices = choices;
        this.success = true;  // Set to true when the response is successful
        this.errorMessage = null;  // No error message when successful
    }

    // Constructor for handling errors
    public LLMResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.choices = null;  // No choices when there's an error
    }

    // Getters and setters
    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static class Choice {
        private Message message;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    public static class Message {
        private String role;
        private String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
