package com.hangoutz.app.controller;

import com.hangoutz.app.model.Category;
import com.hangoutz.app.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<Category> create(@RequestBody Category category) {
        return new ResponseEntity<>(categoryService.create(category), HttpStatus.OK);
    }
}
