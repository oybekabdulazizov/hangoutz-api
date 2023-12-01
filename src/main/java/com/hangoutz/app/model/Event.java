package com.hangoutz.app.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "description")
    private String description;

    @Column(name = "category")
    private String category;

    @Column(name = "city")
    private String city;

    @Column(name = "venue")
    private String venue;


    // constructors ==========================================
    public Event() {
    }

    public Event(String title, LocalDateTime dateTime, String description, String category, String city, String venue) {
        this.title = title;
        setDate(dateTime);
        this.description = description;
        this.category = category;
        this.city = city;
        this.venue = venue;
    }
    // =======================================================


    // getters and setters ===================================
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

    public void setDate(LocalDateTime dateTime) {
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
    // =======================================================


    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", city='" + city + '\'' +
                ", venue='" + venue + '\'' +
                '}';
    }
}


