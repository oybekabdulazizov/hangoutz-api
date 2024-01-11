package com.hangoutz.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UpdateEventDTO {

    @Length(max = 100, message = "Title cannot be longer than 100 characters")
    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime startDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime finishDateTime;

    @Length(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;

    private String category;

    @Length(max = 255, message = "Location cannot be longer than 255 characters")
    private String location;

    @URL
    @Length(max = 255, message = "URL cannot exceed 255 characters")
    private String url;
}
