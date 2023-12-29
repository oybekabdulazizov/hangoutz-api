package com.hangoutz.app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "event")
public class Event {

    @ManyToMany(
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH})
    @JoinTable(
            name = "event_attendee",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> attendees;

    @ManyToOne(
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH})
    @JoinColumn(name = "host_user_id")
    private User host;

    @ManyToOne(
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH})
    @JoinColumn(name = "category_id")
    private Category category;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime dateTime;

    @Column(name = "description")
    private String description;

    @Column(name = "city")
    private String city;

    @Column(name = "cancelled")
    private boolean cancelled;


    public void addAttendee(User attendee) {
        if (attendees == null) {
            attendees = new ArrayList<>();
        }
        attendees.add(attendee);
    }

    public void removeAttendee(User attendee) {
        if (attendees == null) {
            attendees = new ArrayList<>();
        } else {
            attendees.remove(attendee);
        }
    }


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
                ", categoryId='" + category.getId() + '\'' +
                ", city='" + city + '\'' +
                ", isCancelled=" + cancelled +
                ", attendeesCount=" + attendees.size() +
                ", hostUserId='" + host.getId() + '\'' +
                '}';
    }
}
