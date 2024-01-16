package com.hangoutz.app.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UserProfile {

    private String id;

    private String name;

    private String lastname;

    private String email;
}
