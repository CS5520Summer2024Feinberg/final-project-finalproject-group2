package edu.northeastern.group2final.suggestion.model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Suggestion {
    private String prompt;
    private String content;
    private String id; // FireStore documentID
    private String userId;
    private Date createdAt;

    public Suggestion() {

    }
    public Suggestion(String prompt, String content) {
        this.prompt = prompt;
        this.content = content;
    }

    public Suggestion(String userId, String prompt, String content) {
        this.userId = userId;
        this.prompt = prompt;
        this.content = content;
        this.createdAt = new Date();
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMM-dd-yyyy' Time: 'HH:mm", Locale.getDefault());
        return sdf.format(this.createdAt);
    }
    public String getPrompt() {
        return prompt;
    }
    public String getContent() {
        return content;
    }
    public void setPrompt(String prompt) {this.prompt = prompt;}
    public void setContent(String content) {
        this.content = content;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setCreatedAt(Date createdAt) {this.createdAt = createdAt;}
    public Date getCreatedAt() {return createdAt;}
    public Timestamp getCreatedAtTimestamp() { return new Timestamp(createdAt);}
    public void setCreatedAtFromTimeStamp(Timestamp timestamp) {this.createdAt = timestamp.toDate();}
}