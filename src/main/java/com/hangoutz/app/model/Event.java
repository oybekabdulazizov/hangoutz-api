package com.hangoutz.app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "event")
public class Event {

    @ManyToOne(
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH})
    @JoinColumn(name = "host_user_id")
    private User host;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;

    @Column(name = "description")
    private String description;

    @Column(name = "category")
    private String category;

    @Column(name = "city")
    private String city;

    @Column(name = "venue")
    private String venue;

    @JsonBackReference
    public User getHost() {
        return host;
    }

    @JsonBackReference
    public void setUser(User host) {
        this.host = host;
    }

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
                ", hostUserId='" + host.getId() + '\'' +
                '}';
    }
}
