package com.example.a5236.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private static String userId;

    public LoggedInUser(String userId) {
        this.userId = userId;
    }

    public static String getUserId() {
        return userId;
    }

}