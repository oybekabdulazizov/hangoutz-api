package com.hangoutz.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UpdateUserDTO {

    private String name;

    private String lastname;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime dateOfBirth;

    private String email;
}
