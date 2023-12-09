package com.hangoutz.app.service;

import com.hangoutz.app.dao.UserDAO;
import com.hangoutz.app.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

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
    @Transactional
    public void save(User user) {
        userDAO.save(user);
    }

    @Override
    @Transactional
    public void delete(User user) {
        userDAO.delete(user);
    }

    @Override
    @Transactional
    public void update(User user) {
        userDAO.update(user);
    }
}
