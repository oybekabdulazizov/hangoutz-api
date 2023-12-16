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
public class UserDTO {

    private String id;

    private String name;

    private String lastname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateOfBirth;

    private String email;

    private List<EventProfile> hostingEvents;

    private List<EventProfile> attendingEvents;

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", email='" + email + '\'' +
                ", hostingEvents=" + hostingEvents +
                ", attendingEvents=" + attendingEvents +
                '}';
    }
}

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
class EventProfile {

    private String id;

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;

    private String description;

    private SimpleCategoryDTO category;

    private String city;

    private boolean cancelled;
}