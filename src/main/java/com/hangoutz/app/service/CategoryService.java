package com.hangoutz.app.service;

import com.hangoutz.app.model.Category;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    Category findById(String id);

    Category create(Category newCategory) throws BadRequestException;
}
