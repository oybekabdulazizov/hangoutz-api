package com.hangoutz.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UpdateEventDTO {

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime startDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime finishDateTime;

    private String description;

    private String category;

    private String city;

    @URL
    private String url;
}
