package com.example.scraper.model;



public class EntityData {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "EntityData{" +
                "title='" + title + '\'' +
                '}';
    }
}
