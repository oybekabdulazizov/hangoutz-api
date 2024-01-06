package com.hangoutz.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponseDTO {

    private String sessionToken;

    private String refreshToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime sessionTokenExpiresAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime refreshTokenExpiresAt;
}
