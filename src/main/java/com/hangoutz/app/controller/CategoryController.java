package com.hangoutz.app.controller;

import com.hangoutz.app.dto.CategoryDTO;
import com.hangoutz.app.dto.NewCategoryDTO;
import com.hangoutz.app.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<CategoryDTO> categories = categoryService.findAll();
        return categories.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable String id) {
        return new ResponseEntity<>(categoryService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> create(@Valid @RequestBody NewCategoryDTO newCategoryDTO) {
        return new ResponseEntity<>(categoryService.create(newCategoryDTO), HttpStatus.OK);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> update(
            @PathVariable String id,
            @RequestBody Map<String, String> updatedFields
    ) {
        return new ResponseEntity<>(categoryService.update(id, updatedFields), HttpStatus.OK);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void delete(@PathVariable String id) {
        categoryService.delete(id);
    }
}
