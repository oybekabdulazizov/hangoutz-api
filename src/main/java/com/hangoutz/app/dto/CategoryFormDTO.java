package com.hangoutz.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CategoryFormDTO {

    @NotBlank(message = "category name is required")
    private String name;
}
