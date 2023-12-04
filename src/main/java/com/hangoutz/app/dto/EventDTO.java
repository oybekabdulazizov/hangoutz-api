package com.hangoutz.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class EventDTO {

    private String id;

    @NotBlank(message = "title is required")
    private String title;

    @NotNull(message = "date time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "category is required")
    private String category;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "venue is required")
    private String venue;

    public EventDTO() {
    }

    public EventDTO(String id, String title, LocalDateTime dateTime, String description, String category, String city, String venue) {
        this.id = id;
        this.title = title;
        this.dateTime = dateTime;
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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
