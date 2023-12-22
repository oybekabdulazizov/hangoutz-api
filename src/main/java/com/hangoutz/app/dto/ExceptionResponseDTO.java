package com.hangoutz.app.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ExceptionResponseDTO {

    private String message;

    private int status;
}
