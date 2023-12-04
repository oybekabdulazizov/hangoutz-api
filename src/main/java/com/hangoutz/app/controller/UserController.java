package com.hangoutz.app.controller;

import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.model.User;
import com.hangoutz.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<Collection<User>> findAll() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> findById(@PathVariable String userId) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/users/find-by-email")
    public ResponseEntity<User> findByEmailAddress(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Request param 'email' cannot be null or blank");
        }
        User user = userService.findByEmailAddress(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<User> create(@RequestBody User newUser) {
        userService.save(newUser);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
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
