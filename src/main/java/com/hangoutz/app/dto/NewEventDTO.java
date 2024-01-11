package com.hangoutz.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class NewEventDTO {

    @NotBlank(message = "title is required")
    @Length(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @NotNull(message = "start time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime startDateTime;

    @NotNull(message = "finish time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime finishDateTime;

    @NotBlank(message = "description is required")
    @Length(min = 2, max = 500, message = "Description must be between 2 and 500 characters")
    private String description;

    @NotBlank(message = "category is required")
    private String category;

    @NotBlank(message = "location is required")
    @Length(min = 2, max = 255, message = "Location must be between 2 and 255 characters")
    private String location;

    @URL
    @Length(min = 2, max = 255, message = "URL must be between 2 and 255 characters")
    private String url;
}
