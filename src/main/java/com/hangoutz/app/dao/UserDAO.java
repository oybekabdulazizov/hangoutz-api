package com.hangoutz.app.dao;

import com.hangoutz.app.model.User;

import java.util.List;

public interface UserDAO {

    List<User> findAll();

    User findById(String id);

    User findByEmail(String email);

    void save(User user);

    void delete(User user);

    User update(User user);
}
