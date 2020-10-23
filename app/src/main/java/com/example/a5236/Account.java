package com.example.a5236;

import java.util.List;

public class Account {
    private String username;
    private String password;
    private List<Account> friends;
    private int score;

    public Account(String username, String password/*, List<Account> friends*/, int score) {
        this.username = username;
        this.password = password;
//        this.friends = friends;
        this.score = score;
    }

    public List<Account> getFriends() {
        return friends;
    }

    public void setFriends(List<Account> friends) {
        this.friends = friends;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
