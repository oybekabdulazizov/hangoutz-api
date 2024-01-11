package com.hangoutz.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDTO {

    @NotBlank(message = "name is required")
    @Length(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "lastname is required")
    @Length(min = 2, max = 100, message = "Lastname must be between 2 and 100 characters")
    private String lastname;

    @NotNull(message = "date of birth is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate dateOfBirth;

    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;
}
