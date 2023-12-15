package com.hangoutz.app.controller;

import com.hangoutz.app.model.Category;
import com.hangoutz.app.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Category> create(@RequestBody Category category) {
        return new ResponseEntity<>(categoryService.create(category), HttpStatus.OK);
    }
}
