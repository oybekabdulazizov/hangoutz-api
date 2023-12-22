package com.hangoutz.app.service;

import com.hangoutz.app.dao.UserDAO;
import com.hangoutz.app.dto.UserDTO;
import com.hangoutz.app.exception.BadRequestException;
import com.hangoutz.app.exception.ExceptionMessage;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.mappers.UserMapper;
import com.hangoutz.app.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final UserMapper userMapper;

    @Override
    public List<UserDTO> findAll() {
        List<UserDTO> users = userDAO.findAll().stream()
                                     .map((user) -> userMapper.toDto(user)).toList();
        return users;
    }

    @Override
    public UserDTO findById(String id) {
        User user = checkByIdIfUserExists(id);
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO findByEmail(String email) {
        User user = checkByUsernameIfUserExists(email);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void delete(String id) {
        userDAO.delete(checkByIdIfUserExists(id));
    }

    @Override
    @Transactional
    public UserDTO update(String id, Map<Object, Object> updatedFields) {
        User user = checkByIdIfUserExists(id);
        updatedFields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(User.class, (String) key);
            if (field != null && !(key.equals("id") || key.equals("password"))) {
                field.setAccessible(true);
                if (value == null || value.toString().isBlank()) {
                    throw new BadRequestException(key + " is required");
                }
                if (key == "dateOfBirth") {
                    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime ldt = LocalDateTime.parse(value.toString(), dateTimeFormat);
                    ReflectionUtils.setField(field, user, ldt);
                } else if (userDAO.findByEmail(value.toString()) != null) {
                    throw new BadRequestException(ExceptionMessage.EMAIL_TAKEN);
                } else {
                    ReflectionUtils.setField(field, user, value);
                }
            }
        });
        return userMapper.toDto(userDAO.update(user));
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


    private User checkByUsernameIfUserExists(String username) {
        User user = userDAO.findByEmail(username);
        if (user == null) {
            throw new NotFoundException(ExceptionMessage.USER_NOT_FOUND);
        }
        return user;
    }

    private User checkByIdIfUserExists(String id) {
        User user = userDAO.findById(id);
        if (user == null) {
            throw new NotFoundException(ExceptionMessage.USER_NOT_FOUND);
        }
        return user;
    }
}
