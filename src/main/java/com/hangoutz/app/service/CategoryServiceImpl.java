package com.hangoutz.app.service;

import com.hangoutz.app.dao.CategoryDAO;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.model.Category;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;

    @Override
    public List<Category> findAll() {
        return categoryDAO.findAll();
    }

    @Override
    public Category findById(String id) {
        Category category = categoryDAO.findById(id);
        if (category == null) {
            throw new NotFoundException("Category not found");
        }
        return category;
    }

    @Override
    @Transactional
    public Category create(Category newCategory) {
        categoryDAO.save(newCategory);
        return newCategory;
    }
}
