package edu.northeastern.group2final.onboarding.util;

public class AuthEvent {
    private final Type type;
    private final String userId;
    private final String displayName;
    public AuthEvent(Type type, String userId, String displayName) {
        this.type = type;
        this.userId = userId;
        this.displayName = displayName;
    }

    public Type getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public enum Type {
        SIGN_IN_SUCCESS,
        SIGN_UP_SUCCESS
    }
}
