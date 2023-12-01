package com.hangoutz.app.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class EventDTO {

    private String id;

    private String title;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;

    private String description;

    private String category;

    private String city;

    private String venue;

    public EventDTO() {
    }

    public EventDTO(String id, String title, LocalDateTime date, String description, String category, String city, String venue) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
        this.category = category;
        this.city = city;
        this.venue = venue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}
