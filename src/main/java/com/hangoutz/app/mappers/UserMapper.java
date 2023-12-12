package com.hangoutz.app.mappers;

import com.hangoutz.app.dto.UserDTO;
import com.hangoutz.app.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDto(User user, UserDTO userDTO) {
        new ModelMapper()
                .typeMap(User.class, UserDTO.class)
                .map(user, userDTO);
        return userDTO;
    }
}
