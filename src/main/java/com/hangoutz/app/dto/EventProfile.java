package com.hangoutz.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class EventProfile {

    private String id;

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime startDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime finishDateTime;

    private String description;

    private CategoryProfile category;

    private String city;

    private String url;

    private boolean cancelled;
}
