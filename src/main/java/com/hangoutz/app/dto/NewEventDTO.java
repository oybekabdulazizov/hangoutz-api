package com.hangoutz.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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
    private String title;

    @NotNull(message = "start time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime startDateTime;

    @NotNull(message = "finish time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime finishDateTime;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "category is required")
    private String category;

    @NotBlank(message = "location is required")
    private String location;

    @URL
    private String url;
}
