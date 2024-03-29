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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime startDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime finishDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime lastModifiedAt;

    private String description;

    private String location;

    private String url;

    private String thumbnailUrl;

    private boolean cancelled;

    private UserProfile host;

    private CategoryProfile category;

    private List<UserProfile> attendees;

    @Override
    public String toString() {
        return "NewEventDTO{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", startDateTime=" + startDateTime +
                ", finishDateTime=" + finishDateTime +
                ", createdAt=" + createdAt +
                ", lastModifiedAt=" + lastModifiedAt +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                ", cancelled=" + cancelled +
                ", host='" + host + '\'' +
                ", attendees=" + attendees +
                '}';
    }
}

