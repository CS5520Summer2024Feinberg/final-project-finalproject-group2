package edu.northeastern.group2final.model;

public class Suggestion {
    private String prompt;
    private String content;

    public Suggestion(String prompt, String content) {
        this.prompt = prompt;
        this.content = content;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getContent() {
        return content;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
