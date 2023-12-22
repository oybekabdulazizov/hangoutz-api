package com.hangoutz.app.service;

import com.hangoutz.app.dto.CategoryDTO;
import com.hangoutz.app.dto.CategoryFormDTO;

import java.util.List;

public interface CategoryService {

    List<CategoryDTO> findAll();

    CategoryDTO findById(String id);

    CategoryDTO findByName(String name);

    CategoryDTO create(CategoryFormDTO newCategoryDTO);

    CategoryDTO update(String id, CategoryFormDTO updatedCategoryDTO);

    void delete(String id);
}
