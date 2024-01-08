package com.hangoutz.app.controller;

import com.hangoutz.app.dto.UpdateUserDTO;
import com.hangoutz.app.dto.UserDTO;
import com.hangoutz.app.exception.BadRequestException;
import com.hangoutz.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;


    @GetMapping("/users")
    public ResponseEntity<Collection<UserDTO>> findAll() {
        List<UserDTO> users = userService.findAll();
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable String id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }


    @GetMapping("/users/find-by-email")
    public ResponseEntity<UserDTO> findByEmail(@RequestParam String email) {
        if (email == null || email.isBlank())
            throw new BadRequestException("Request parameter 'email' cannot be null or blank");
        return new ResponseEntity<>(userService.findByEmail(email), HttpStatus.OK);
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> update(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String bearerToken,
            @PathVariable String id,
            @RequestBody UpdateUserDTO updatedFields
    ) {
        return new ResponseEntity<>(userService.update(bearerToken, id, updatedFields), HttpStatus.OK);
    }


    @DeleteMapping("/users/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void delete(@PathVariable String id) {
        userService.delete(id);
    }
}
