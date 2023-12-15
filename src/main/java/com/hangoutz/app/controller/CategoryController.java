package com.hangoutz.app.controller;

import com.hangoutz.app.model.Category;
import com.hangoutz.app.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> findAll() {
        List<Category> events = categoryService.findAll();
        HttpStatus httpStatus = events.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(events, httpStatus);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> findById(@PathVariable String id) {
        return new ResponseEntity<>(categoryService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/categories")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity<Category> create(@RequestBody Category category) throws BadRequestException {
        return new ResponseEntity<>(categoryService.create(category), HttpStatus.OK);
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity<Category> update(@PathVariable String id, @RequestBody Category category) throws BadRequestException {
        return new ResponseEntity<>(categoryService.update(id, category), HttpStatus.OK);
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity delete(@PathVariable String id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
