package com.hangoutz.app.mappers;

import com.hangoutz.app.dto.SignUpRequestDTO;
import com.hangoutz.app.dto.UserDTO;
import com.hangoutz.app.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDto(User model) {
        UserDTO dto = new UserDTO();
        new ModelMapper()
                .typeMap(User.class, UserDTO.class)
                .map(model, dto);
        return dto;
    }

    public User toModel(SignUpRequestDTO dto) {
        User model = new User();
        new ModelMapper()
                .typeMap(SignUpRequestDTO.class, User.class)
                .map(dto, model);
        return model;
    }
}
