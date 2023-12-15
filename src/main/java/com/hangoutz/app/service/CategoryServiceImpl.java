package com.hangoutz.app.service;

import com.hangoutz.app.dao.CategoryDAO;
import com.hangoutz.app.model.Category;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;

    @Override
    @Transactional
    public Category create(Category newCategory) {
        categoryDAO.save(newCategory);
        return newCategory;
    }
}
