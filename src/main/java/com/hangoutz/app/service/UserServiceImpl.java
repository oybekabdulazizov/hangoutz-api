package com.hangoutz.app.service;

import com.hangoutz.app.dao.UserDAO;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        User user = userDAO.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    @Override
    public User findByEmailAddress(String emailAddress) {
        User user = userDAO.findByEmailAddress(emailAddress);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
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

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = findByEmailAddress(username);
                if (user == null) {
                    throw new NotFoundException("(userDetailsService.loadUserByUsername()): User not found");
                }
                return user;
            }
        };
    }
}
