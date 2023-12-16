package com.hangoutz.app.service;

import com.hangoutz.app.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<User> findAll();

    User findById(String id);

    User findByEmail(String email);

    User create(User user);

    void delete(String id);

    User update(String id, Map<Object, Object> updatedFields);

    UserDetailsService userDetailsService();
}
