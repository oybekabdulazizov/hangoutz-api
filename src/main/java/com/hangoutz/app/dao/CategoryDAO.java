package com.hangoutz.app.dao;

import com.hangoutz.app.model.Category;

import java.util.List;

public interface CategoryDAO {

    List<Category> findAll();

    Category findById(String id);

    void save(Category category);
}
