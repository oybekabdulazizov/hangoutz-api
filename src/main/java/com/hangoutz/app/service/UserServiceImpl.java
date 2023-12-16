package com.hangoutz.app.service;

import com.hangoutz.app.dao.UserDAO;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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
    public User findByEmail(String email) {
        User user = userDAO.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    @Override
    @Transactional
    public User create(User user) {
        return userDAO.save(user);
    }

    @Override
    @Transactional
    public void delete(String id) {
        userDAO.delete(findById(id));
    }

    @Override
    @Transactional
    public User update(String id, Map<Object, Object> updatedFields) {
        User user = findById(id);
        updatedFields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(User.class, (String) key);
            if (field != null && !(key.equals("id") || key.equals("password"))) {
                field.setAccessible(true);
                if (value == null || value.toString().isBlank()) {
                    throw new IllegalArgumentException(key + " is required");
                }
                if (key == "dateOfBirth") {
                    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime ldt = LocalDateTime.parse(value.toString(), dateTimeFormat);
                    ReflectionUtils.setField(field, user, ldt);
                } else if (userDAO.findByEmail(value.toString()) != null) {
                    try {
                        throw new BadRequestException("User with this email already exists.");
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    ReflectionUtils.setField(field, user, value);
                }
            }
        });
        return userDAO.update(user);
    }

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userDAO.findByEmail(username);
            }
        };
    }
}
