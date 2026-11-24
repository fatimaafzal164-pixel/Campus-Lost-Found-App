package com.example.campuslostfound;

public class itemModel {
    String title, location, description, status, imageBase64;

    public itemModel() {}

    public itemModel(String title, String location, String description, String status, String imageBase64) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.status = status;
        this.imageBase64 = imageBase64;
    }

    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getImageBase64() { return imageBase64; }
}