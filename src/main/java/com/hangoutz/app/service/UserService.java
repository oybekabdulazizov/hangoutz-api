package com.hangoutz.app.service;

import com.hangoutz.app.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(String id);

    User findByEmailAddress(String emailAddress);

    void save(User user);

    void delete(User user);

    void update(User user);

    UserDetailsService userDetailsService();
}
