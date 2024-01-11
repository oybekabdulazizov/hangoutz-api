package com.hangoutz.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class NewCategoryDTO {

    @NotBlank(message = "category name is required")
    @Length(min = 2, max = 100, message = "category must be between 2 and 100 characters")
    private String name;
}
