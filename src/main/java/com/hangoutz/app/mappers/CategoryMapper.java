package com.hangoutz.app.mappers;

import com.hangoutz.app.dto.CategoryDTO;
import com.hangoutz.app.dto.CategoryFormDTO;
import com.hangoutz.app.model.Category;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    public CategoryDTO toDto(Category category) {
        CategoryDTO dto = new ModelMapper().map(category, CategoryDTO.class);
        dto.setNumberOfEvents(category.getEvents().size());
        return dto;
    }

    public Category toModel(CategoryFormDTO categoryDto) {
        return new ModelMapper().map(categoryDto, Category.class);
    }
}
