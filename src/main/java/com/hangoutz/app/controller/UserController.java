package com.hangoutz.app.controller;

import com.hangoutz.app.dto.UserDTO;
import com.hangoutz.app.mappers.UserMapper;
import com.hangoutz.app.model.User;
import com.hangoutz.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/users")
    public ResponseEntity<Collection<UserDTO>> findAll() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        List<UserDTO> usersDTO = new ArrayList<>();
        users.forEach((user) -> {
            usersDTO.add(userMapper.modelToDto(user));
        });
        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> findById(@PathVariable String userId) {
        User user = userService.findById(userId);
        return new ResponseEntity<>(userMapper.modelToDto(user), HttpStatus.OK);
    }

    @GetMapping("/users/find-by-email")
    public ResponseEntity<UserDTO> findByEmailAddress(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Request parameter 'email' cannot be null or blank");
        }
        User user = userService.findByEmailAddress(email);
        return new ResponseEntity<>(userMapper.modelToDto(user), HttpStatus.OK);
    }

    /*
    @PostMapping("/users")
    public ResponseEntity<UserDTO> create(@Valid @RequestBody RegisterUserDTO regUserDTO) {
        User newUser = userMapper.regUserDtoToModel(regUserDTO);
        userService.save(newUser);
        return new ResponseEntity<>(userMapper.modelToDto(newUser), HttpStatus.CREATED);
    }
    */

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDTO> update(@PathVariable String userId, @RequestBody Map<Object, Object> fields) {
        User user = userService.findById(userId);

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
        userService.delete(user);
        return new ResponseEntity<>("Deleted the user with id " + userId, HttpStatus.OK);
    }
}
