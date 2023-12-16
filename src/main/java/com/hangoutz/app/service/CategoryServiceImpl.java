package com.hangoutz.app.service;

import com.hangoutz.app.dao.CategoryDAO;
import com.hangoutz.app.dao.EventDAO;
import com.hangoutz.app.dto.CategoryDTO;
import com.hangoutz.app.dto.CategoryFormDTO;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.mappers.CategoryMapper;
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
    private final CategoryMapper categoryMapper;
    private final EventDAO eventDAO;

    @Override
    public List<CategoryDTO> findAll() {
        List<CategoryDTO> categories = categoryDAO
                .findAll().stream()
                .map((category -> categoryMapper.toDto(category, new CategoryDTO()))).toList();
        return categories;
    }

    @Override
    public CategoryDTO findById(String id) {
        Category existingCategory = checkByIdIfCategoryExists(id);
        return categoryMapper.toDto(existingCategory, new CategoryDTO());
    }

    @Override
    public CategoryDTO findByName(String name) {
        Category existingCategory = checkByNameIfCategoryExists(name);
        return categoryMapper.toDto(existingCategory, new CategoryDTO());
    }

    @Override
    @Transactional
    public CategoryDTO create(CategoryFormDTO newCategoryDTO) throws BadRequestException {
        checkByNameIfCategoryAlreadyExists(newCategoryDTO.getName());
        Category newCategory = categoryMapper.formDtoToModel(newCategoryDTO);
        return categoryMapper.toDto(categoryDAO.save(newCategory), new CategoryDTO());
    }

    @Override
    @Transactional
    public CategoryDTO update(String id, CategoryFormDTO updatedCategoryDTO) throws BadRequestException {
        checkByNameIfCategoryAlreadyExists(updatedCategoryDTO.getName());
        Category existingCategory = checkByIdIfCategoryExists(id);
        existingCategory.setName(updatedCategoryDTO.getName());
        return categoryMapper.toDto(categoryDAO.update(existingCategory), new CategoryDTO());
    }

    @Override
    @Transactional
    public void delete(String id) {
        Category existingCategory = checkByIdIfCategoryExists(id);
        existingCategory.getEvents().forEach((event) -> {
            event.setCategory(null);
            eventDAO.update(event);
        });
        existingCategory.setEvents(null);
        categoryDAO.delete(existingCategory);
    }

    private void checkByNameIfCategoryAlreadyExists(String name) throws BadRequestException {
        Category categoryFromDb = categoryDAO.findByName(name);
        if (categoryFromDb != null) {
            throw new BadRequestException("This category already exists");
        }
    }

    private Category checkByNameIfCategoryExists(String name) {
        Category existingCategory = categoryDAO.findByName(name);
        if (existingCategory == null) {
            throw new NotFoundException("Category not found");
        }
        return existingCategory;
    }

    private Category checkByIdIfCategoryExists(String id) {
        Category category = categoryDAO.findById(id);
        if (category == null) {
            throw new NotFoundException("Category not found");
        }
        return category;
    }
}
