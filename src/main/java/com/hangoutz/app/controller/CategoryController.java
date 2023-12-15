package com.hangoutz.app.controller;

import com.hangoutz.app.dto.CategoryDTO;
import com.hangoutz.app.dto.CategoryFormDTO;
import com.hangoutz.app.mappers.CategoryMapper;
import com.hangoutz.app.model.Category;
import com.hangoutz.app.service.CategoryService;
import jakarta.validation.Valid;
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
    private final CategoryMapper categoryMapper;


    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<CategoryDTO> categories = categoryService
                .findAll().stream()
                .map((category -> categoryMapper.toDto(category, new CategoryDTO()))).toList();
        HttpStatus httpStatus = categories.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(categories, httpStatus);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable String id) {
        CategoryDTO category = categoryMapper.toDto(categoryService.findById(id), new CategoryDTO());
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PostMapping("/categories")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> create(@Valid @RequestBody CategoryFormDTO categoryFormDTO)
            throws BadRequestException {
        Category category = categoryService.create(categoryMapper.formDtoToModel(categoryFormDTO));
        return new ResponseEntity<>(categoryMapper.toDto(category, new CategoryDTO()), HttpStatus.OK);
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> update(
            @PathVariable String id,
            @Valid @RequestBody CategoryFormDTO categoryFormDTO
    )
            throws BadRequestException {
        Category updatedCategory = categoryService.update(id, categoryMapper.formDtoToModel(categoryFormDTO));
        return new ResponseEntity<>(categoryMapper.toDto(updatedCategory, new CategoryDTO()), HttpStatus.OK);
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity delete(@PathVariable String id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
