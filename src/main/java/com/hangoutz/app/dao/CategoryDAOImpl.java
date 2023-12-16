package com.hangoutz.app.dao;

import com.hangoutz.app.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryDAOImpl implements CategoryDAO {

    private final EntityManager em;

    @Override
    public List<Category> findAll() {
        TypedQuery<Category> query = em.createQuery("from Category", Category.class);
        return query.getResultList();
    }

    @Override
    public Category findById(String id) {
        return em.find(Category.class, id);
    }

    @Override
    public Category findByName(String name) {
        TypedQuery<Category> query = em.createQuery("from Category where name = :name", Category.class);
        query.setParameter("name", name.toLowerCase());
        return query.getResultList().isEmpty() ? null : query.getSingleResult();
    }

    @Override
    public Category save(Category newCategory) {
        em.persist(newCategory);
        return newCategory;
    }

    @Override
    public Category update(Category category) {
        return em.merge(category);
    }

    @Override
    public void delete(Category category) {
        em.remove(category);
    }
}
