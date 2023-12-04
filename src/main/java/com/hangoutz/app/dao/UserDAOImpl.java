package com.hangoutz.app.dao;

import com.hangoutz.app.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {

    private final EntityManager em;

    @Autowired
    public UserDAOImpl(EntityManager em) {
        this.em = em;
    }

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
    public User findByEmailAddress(String emailAddress) {
        TypedQuery<User> query = em.createQuery("from User where emailAddress=:emailAddress", User.class);
        query.setParameter("emailAddress", emailAddress);
        return query.getSingleResult();
    }

    @Override
    public void save(User user) {
        em.persist(user);
    }


}
