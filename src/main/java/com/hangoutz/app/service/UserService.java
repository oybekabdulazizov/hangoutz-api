package com.hangoutz.app.service;

import com.hangoutz.app.model.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(String id);

    User findByEmailAddress(String emailAddress);

    void save(User user);
}
