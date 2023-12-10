package com.hangoutz.app.dao;

import com.hangoutz.app.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserDAOImpl implements UserDAO {

    private final EntityManager em;

    @Override
    public List<User> findAll() {
        TypedQuery<User> query = em.createQuery("from User", User.class);
        return query.getResultList();
    }

    @Override
    public User findById(String id) {
        return em.find(User.class, id);
    }

    @Override
    @Transactional
    public User findByEmailAddress(String emailAddress) {
        TypedQuery<User> query = em.createQuery("from User where emailAddress=:emailAddress", User.class);
        query.setParameter("emailAddress", emailAddress);
        if (query.getResultList().isEmpty()) {
            return null;
        }
        return query.getSingleResult();
    }

    @Override
    public void save(User user) {
        em.persist(user);
    }

    @Override
    public void delete(User user) {
        em.remove(user);
    }

    @Override
    public void update(User user) {
        em.merge(user);
    }
}
