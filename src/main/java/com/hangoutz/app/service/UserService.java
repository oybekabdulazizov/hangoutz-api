package com.hangoutz.app.service;

import com.hangoutz.app.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<UserDTO> findAll();

    UserDTO findById(String id);

    UserDTO findByEmail(String email);

    void delete(String id);

    UserDTO update(String id, Map<Object, Object> updatedFields);

    UserDetailsService userDetailsService();
}
