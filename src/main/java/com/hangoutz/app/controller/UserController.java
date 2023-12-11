package com.hangoutz.app.controller;

import com.hangoutz.app.dto.UserDTO;
import com.hangoutz.app.mappers.UserMapper;
import com.hangoutz.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping("/users")
    public ResponseEntity<Collection<UserDTO>> findAll() {
        List<UserDTO> users = userService.findAll().stream()
                                         .map((user) -> userMapper.modelToDto(user)).toList();
        HttpStatus httpStatus = users.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(users, httpStatus);
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable String id) {
        return new ResponseEntity<>(userMapper.modelToDto(userService.findById(id)), HttpStatus.OK);
    }


    @GetMapping("/users/find-by-email")
    public ResponseEntity<UserDTO> findByEmail(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Request parameter 'email' cannot be null or blank");
        }
        return new ResponseEntity<>(userMapper.modelToDto(userService.findByEmail(email)), HttpStatus.OK);
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable String id, @RequestBody Map<Object, Object> updatedFields) {
        return new ResponseEntity<>(userMapper.modelToDto(userService.update(id, updatedFields)), HttpStatus.OK);
    }


    @DeleteMapping("/users/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@PathVariable String id) {
        userService.delete(id);
    }
}
