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

    private String category;

    private String city;

    private String venue;

    private AttendeeProfile host;

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