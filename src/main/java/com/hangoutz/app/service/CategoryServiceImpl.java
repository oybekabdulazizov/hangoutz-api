package com.hangoutz.app.service;

import com.hangoutz.app.dao.CategoryDAO;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.model.Category;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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
    public void checkByName(String name) throws BadRequestException {
        Category categoryFromDb = categoryDAO.findByName(name);
        if (categoryFromDb != null) {
            throw new BadRequestException("This category already exists");
        }
    }

    @Override
    @Transactional
    public Category create(Category newCategory) throws BadRequestException {
        checkByName(newCategory.getName());
        return categoryDAO.save(newCategory);
    }

    @Override
    @Transactional
    public Category update(String id, Category category) throws BadRequestException {
        Category existingCategory = findById(id);
        checkByName(category.getName());
        existingCategory.setName(category.getName());
        return categoryDAO.save(existingCategory);
    }
}
