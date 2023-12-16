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

    public CategoryDTO toDto(Category category, CategoryDTO categoryDTO) {
        new ModelMapper()
                .typeMap(Category.class, CategoryDTO.class)
                /*.addMappings(mapper -> {
                    mapper.map(src -> src.getHost().getId(), EventDTO::setHostUserId);
                })*/
                .map(category, categoryDTO);
        categoryDTO.setNumberOfEvents(category.getEvents().size());
        return categoryDTO;
    }

    public Category formDtoToModel(CategoryFormDTO categoryFormDTO) {
        return new ModelMapper().map(categoryFormDTO, Category.class);
    }
}
