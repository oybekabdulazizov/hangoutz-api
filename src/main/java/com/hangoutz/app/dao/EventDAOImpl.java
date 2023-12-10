package com.hangoutz.app.dao;

import com.hangoutz.app.model.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class EventDAOImpl implements EventDAO {

    private final EntityManager em;

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
    public void delete(Event event) {
        em.remove(event);
    }

    @Override
    public Event update(Event event) {
        return em.merge(event);
    }
}
