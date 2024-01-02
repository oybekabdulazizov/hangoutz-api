package com.hangoutz.app.service;

import com.hangoutz.app.dto.UpdateUserDTO;
import com.hangoutz.app.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {

    List<UserDTO> findAll();

    UserDTO findById(String id);

    UserDTO findByEmail(String email);

    void delete(String id);

    UserDTO update(String bearerToken, String id, UpdateUserDTO updatedUserDTO);

    UserDetailsService userDetailsService();
}
