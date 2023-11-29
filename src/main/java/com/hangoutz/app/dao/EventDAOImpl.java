package com.hangoutz.app.dao;

import com.hangoutz.app.model.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventDAOImpl implements EventDAO {

    private final EntityManager em;

    @Autowired
    public EventDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Event> findAll() {
        TypedQuery<Event> query = em.createQuery("from Event", Event.class);
        return query.getResultList();
    }

    @Override
    public Event findById(String id) {
        return em.find(Event.class, id);
    }

    @Override
    public void save(Event event) {
        em.persist(event);
    }

    @Override
    public void delete(String id) {
        Event event = em.find(Event.class, id);
        em.remove(event);
    }

    @Override
    public void update(Event event) {
        em.merge(event);
    }
}
