package com.hangoutz.app.mappers;

import com.hangoutz.app.dto.CategoryFormDTO;
import com.hangoutz.app.model.Category;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    public Category formDtoToModel(CategoryFormDTO categoryFormDTO) {
        return new ModelMapper().map(categoryFormDTO, Category.class);
    }
}
