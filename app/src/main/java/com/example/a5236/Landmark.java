package com.example.a5236;

import android.net.Uri;

import com.example.a5236.data.model.LoggedInUser;

import java.util.List;

public class Landmark {

    private String title;
    private int difficulty;
    private String coordinates;
    private String image;
    private String description;
    private String hint;
    private String createdBy;
    private List<String> foundByUsers;

    public Landmark(String title, int difficulty, String coordinates, String image, String description, String hint, String createdBy, List<String> foundByUsers) {
        this.title = title;
        this.difficulty = difficulty;
        this.coordinates = coordinates;
        this.image = image;
        this.description = description;
        this.hint = hint;
        this.createdBy = createdBy;
        this.foundByUsers = foundByUsers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHint() { return hint; }

    public void setHint(String hint) { this.hint = hint; }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<String> getFoundByUsers() { return foundByUsers; }

    public void setFoundByUsers(List<String> foundByUsers) { this.foundByUsers = foundByUsers; }
}
