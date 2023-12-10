package com.hangoutz.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordDTO {

    @NotBlank(message = "email is required")
    private String emailAddress;

    @NotBlank(message = "old password is required")
    private String oldPassword;

    @NotBlank(message = "new password is required")
    private String newPassword;

    @NotBlank(message = "password confirmation is required")
    private String confirmNewPassword;
}
