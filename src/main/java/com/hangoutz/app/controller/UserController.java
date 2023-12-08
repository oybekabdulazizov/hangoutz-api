package com.hangoutz.app.controller;

import com.hangoutz.app.dto.RegisterUserDTO;
import com.hangoutz.app.dto.UserDTO;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.mappers.UserMapper;
import com.hangoutz.app.model.User;
import com.hangoutz.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    public ResponseEntity<Collection<UserDTO>> findAll() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }

        List<UserDTO> usersDTO = new ArrayList<>();
        for (User user :
                users) {
            UserDTO userDTO = userMapper.modelToDto(user);
            System.out.println(userDTO);
            usersDTO.add(userDTO);
        }
        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> findById(@PathVariable String userId) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return new ResponseEntity<>(userMapper.modelToDto(user), HttpStatus.OK);
    }

    @GetMapping("/users/find-by-email")
    public ResponseEntity<UserDTO> findByEmailAddress(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Request param 'email' cannot be null or blank");
        }
        User user = userService.findByEmailAddress(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return new ResponseEntity<>(userMapper.modelToDto(user), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> create(@Valid @RequestBody RegisterUserDTO regUserDTO) {
        User newUser = userMapper.regUserDtoToModel(regUserDTO);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        userService.save(newUser);
        return new ResponseEntity<>(userMapper.modelToDto(newUser), HttpStatus.CREATED);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> logIn(@RequestParam String email, @RequestParam String password) {
        User userFromDb = userService.findByEmailAddress(email);
        if (userFromDb == null) {
            throw new NotFoundException("User not found");
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(password, userFromDb.getPassword())) {
            return new ResponseEntity<>("Failed to log in", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("Logged in successfully", HttpStatus.OK);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDTO> update(@PathVariable String userId, @RequestBody Map<Object, Object> fields) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(User.class, (String) key);
            if (field != null && !key.equals("id")) {
                field.setAccessible(true);
                if (value == null || value.toString().isBlank()) {
                    throw new IllegalArgumentException(key + " is required");
                }
                if (key == "dateOfBirth") {
                    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime ldt = LocalDateTime.parse(value.toString(), dateTimeFormat);
                    ReflectionUtils.setField(field, user, ldt);
                } else {
                    ReflectionUtils.setField(field, user, value);
                }
            }
        });
        userService.update(user);
        return new ResponseEntity<>(userMapper.modelToDto(user), HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> delete(@PathVariable String userId) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        userService.delete(user);
        return new ResponseEntity<>("Deleted the user with id " + userId, HttpStatus.OK);
    }
}
