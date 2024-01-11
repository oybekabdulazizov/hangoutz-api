package com.hangoutz.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UpdateUserDTO {

    @Length(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Length(max = 100, message = "Lastname cannot exceed 100 characters")
    private String lastname;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate dateOfBirth;

    private String email;
}
