package com.hangoutz.app.dao;

import com.hangoutz.app.model.Category;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryDAOImpl implements CategoryDAO {

    private final EntityManager em;

    @Override
    public void save(Category newCategory) {
        em.persist(newCategory);
    }
}
