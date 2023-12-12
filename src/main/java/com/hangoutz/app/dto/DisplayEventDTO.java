package com.hangoutz.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class DisplayEventDTO {

    private String id;

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;

    private String description;

    private String category;

    private String city;

    private String venue;

    private String hostUserId;

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
                ", hostUserId='" + hostUserId + '\'' +
                '}';
    }
}

