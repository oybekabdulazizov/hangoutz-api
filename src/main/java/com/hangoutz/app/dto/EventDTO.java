package com.hangoutz.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class EventDTO {

    private String id;

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;

    private String description;

    private String city;

    private String venue;

    private boolean cancelled;

    private AttendeeProfile host;

    private CategoryProfile category;

    private List<AttendeeProfile> attendees;

    @Override
    public String toString() {
        return "NewEventDTO{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", city='" + city + '\'' +
                ", venue='" + venue + '\'' +
                ", cancelled=" + cancelled +
                ", host='" + host + '\'' +
                ", attendees=" + attendees +
                '}';
    }
}

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
class AttendeeProfile {

    private String id;

    private String name;

    private String lastname;

    private String email;
}

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
class CategoryProfile {

    private String id;

    private String name;
}