package com.hangoutz.app.service;

import com.hangoutz.app.dao.EventDAO;
import com.hangoutz.app.dto.CategoryDTO;
import com.hangoutz.app.dto.CategoryFormDTO;
import com.hangoutz.app.exception.BadRequestException;
import com.hangoutz.app.exception.ExceptionMessage;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.mappers.CategoryMapper;
import com.hangoutz.app.model.Category;
import com.hangoutz.app.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventDAO eventDAO;

    @Override
    public List<CategoryDTO> findAll() {
        List<CategoryDTO> categories = categoryRepository
                .findAll().stream()
                .map((category -> categoryMapper.toDto(category))).toList();
        return categories;
    }

    @Override
    public CategoryDTO findById(String id) {
        Category existingCategory = checkByIdIfCategoryExists(id);
        return categoryMapper.toDto(existingCategory);
    }

    @Override
    public CategoryDTO findByName(String name) {
        Category existingCategory = checkByNameIfCategoryExists(name);
        return categoryMapper.toDto(existingCategory);
    }

    @Override
    @Transactional
    public CategoryDTO create(CategoryFormDTO newCategoryDTO) {
        checkByNameIfCategoryAlreadyExists(newCategoryDTO.getName());
        Category newCategory = categoryMapper.toModel(newCategoryDTO);
        return categoryMapper.toDto(categoryRepository.save(newCategory));
    }

    @Override
    @Transactional
    public CategoryDTO update(String id, CategoryFormDTO updatedCategoryDTO) {
        checkByNameIfCategoryAlreadyExists(updatedCategoryDTO.getName());
        Category existingCategory = checkByIdIfCategoryExists(id);
        existingCategory.setName(updatedCategoryDTO.getName());
        return categoryMapper.toDto(categoryRepository.save(existingCategory));
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
        categoryRepository.delete(existingCategory);
    }

    private void checkByNameIfCategoryAlreadyExists(String name) {
        Optional<Category> categoryFromDb = categoryRepository.findByName(name);
        if (categoryFromDb.isPresent()) throw new BadRequestException(ExceptionMessage.CATEGORY_ALREADY_EXISTS);
    }

    private Category checkByNameIfCategoryExists(String name) {
        Optional<Category> existingCategory = categoryRepository.findByName(name);
        if (existingCategory.isEmpty()) throw new NotFoundException(ExceptionMessage.CATEGORY_NOT_FOUND);
        return existingCategory.get();
    }

    private Category checkByIdIfCategoryExists(String id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) throw new NotFoundException(ExceptionMessage.CATEGORY_NOT_FOUND);
        return category.get();
    }
}
