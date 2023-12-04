package com.hangoutz.app.service;

import com.hangoutz.app.dao.UserDAO;
import com.hangoutz.app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    public User findById(String id) {
        return userDAO.findById(id);
    }

    @Override
    public User findByEmailAddress(String emailAddress) {
        return userDAO.findByEmailAddress(emailAddress);
    }

    @Override
    public void save(User user) {
        userDAO.save(user);
    }
}
