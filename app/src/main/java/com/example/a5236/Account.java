package com.example.a5236;

public class Account {
    private String username;
    private String password;
    private int score;

    public Account(String username, String password, int score) {
        this.username = username;
        this.password = password;
        this.score = score;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String newPassword){
        this.password = newPassword;
    }
}
