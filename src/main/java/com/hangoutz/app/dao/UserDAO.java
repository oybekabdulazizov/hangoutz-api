package com.hangoutz.app.dao;

import com.hangoutz.app.model.User;

import java.util.List;

public interface UserDAO {

    List<User> findAll();

    User findById(String id);
    
    void save(User user);
}
