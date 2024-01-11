package com.hangoutz.app.service;

import com.hangoutz.app.dto.CategoryDTO;
import com.hangoutz.app.dto.NewCategoryDTO;
import com.hangoutz.app.exception.BadRequestException;
import com.hangoutz.app.exception.ExceptionMessage;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.mappers.CategoryMapper;
import com.hangoutz.app.model.Category;
import com.hangoutz.app.repository.CategoryRepository;
import com.hangoutz.app.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream().map(categoryMapper::toDto).toList();
    }

    @Override
    public CategoryDTO findById(String id) {
        return categoryMapper.toDto(getByIdIfCategoryExists(id));
    }

    @Override
    public CategoryDTO findByName(String name) {
        return categoryMapper.toDto(getByNameIfCategoryExists(name));
    }

    @Override
    @Transactional
    public CategoryDTO create(NewCategoryDTO newCategoryDTO) {
        checkByNameIfCategoryAlreadyExists(newCategoryDTO.getName());
        Category newCategory = categoryMapper.toModel(newCategoryDTO);
        newCategory.setName(newCategoryDTO.getName().toLowerCase());
        return categoryMapper.toDto(categoryRepository.save(newCategory));
    }

    @Override
    @Transactional
    public CategoryDTO update(String id, Map<String, String> updatedFields) {
        Category categoryToBeUpdated = getByIdIfCategoryExists(id);
        String categoryName = updatedFields.get("name");
        if (categoryName != null && !categoryName.isBlank()) {
            if (categoryName.length() > 255)
                throw new BadRequestException("Category name cannot exceed 255 characters");
            checkByNameIfCategoryAlreadyExists(categoryName);
            categoryToBeUpdated.setName(categoryName.toLowerCase());
            categoryRepository.save(categoryToBeUpdated);
        }
        return categoryMapper.toDto(categoryToBeUpdated);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Category existingCategory = getByIdIfCategoryExists(id);
        existingCategory.getEvents().forEach((event) -> {
            event.setCategory(null);
            eventRepository.save(event);
        });
        existingCategory.setEvents(null);
        categoryRepository.delete(existingCategory);
    }

    private void checkByNameIfCategoryAlreadyExists(String name) {
        Optional<Category> categoryFromDb = categoryRepository.findByName(name);
        if (categoryFromDb.isPresent()) throw new BadRequestException(ExceptionMessage.CATEGORY_ALREADY_EXISTS);
    }

    private Category getByNameIfCategoryExists(String name) {
        Optional<Category> existingCategory = categoryRepository.findByName(name);
        if (existingCategory.isEmpty()) throw new NotFoundException(ExceptionMessage.CATEGORY_NOT_FOUND);
        return existingCategory.get();
    }

    private Category getByIdIfCategoryExists(String id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) throw new NotFoundException(ExceptionMessage.CATEGORY_NOT_FOUND);
        return category.get();
    }
}
