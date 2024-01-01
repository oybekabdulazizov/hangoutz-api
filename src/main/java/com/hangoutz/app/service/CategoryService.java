package com.hangoutz.app.service;

import com.hangoutz.app.dto.CategoryDTO;
import com.hangoutz.app.dto.NewCategoryDTO;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    List<CategoryDTO> findAll();

    CategoryDTO findById(String id);

    CategoryDTO findByName(String name);

    CategoryDTO create(NewCategoryDTO newCategoryDTO);

    CategoryDTO update(String id, Map<String, String> updatedFields);

    void delete(String id);
}
