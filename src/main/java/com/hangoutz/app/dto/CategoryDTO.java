package com.hangoutz.app.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CategoryDTO {

    private String id;

    private String name;

    private int numberOfEvents;

    @Override
    public String toString() {
        return "CategoryDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
