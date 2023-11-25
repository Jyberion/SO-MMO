package com.client.auth;

public class AuthServerMessage {

    private AuthServerMessageType type;
    private String message;
    private String username;
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthServerMessage(AuthServerMessageType type) {
        this(type, null); // Initialize message as null for simplicity
    }

    public AuthServerMessage(AuthServerMessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    public AuthServerMessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public static AuthServerMessage parseMessage(String responseMessage) {
        if (responseMessage == null) {
            throw new IllegalArgumentException("Response message cannot be null");
        }

        String[] messageParts = responseMessage.split(":");
        
        if (messageParts.length != 2) {
            throw new IllegalArgumentException("Invalid message format: " + responseMessage);
        }

        AuthServerMessageType type = AuthServerMessageType.valueOf(messageParts[0]);
        String message = messageParts[1];

        return new AuthServerMessage(type, message);
    }

    @Override
    public String toString() {
        return type + ":" + (message != null ? message : ""); // Handle null message more gracefully
    }
}