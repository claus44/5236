package com.example.a5236;

import android.net.Uri;

import java.util.List;

public class Landmark {

    private String title;
    private int difficulty;
    private String coordinates;
    private String image;
    private String description;
    private String hint;
    private Account createdBy;

    public Landmark(String title, int difficulty, String coordinates, String image, String description, String hint, Account createdBy) {
        this.title = title;
        this.difficulty = difficulty;
        this.coordinates = coordinates;
        this.image = image;
        this.description = description;
        this.hint = hint;
        this.createdBy = createdBy;
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

    public Account getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Account createdBy) {
        this.createdBy = createdBy;
    }
}
