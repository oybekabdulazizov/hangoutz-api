package com.hangoutz.app.service;

import com.hangoutz.app.dao.EventDAO;
import com.hangoutz.app.model.Event;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventDAO eventDAO;

    @Autowired
    public EventServiceImpl(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    @Override
    public List<Event> findAll() {
        return eventDAO.findAll();
    }

    @Override
    public Event findById(String id) {
        return eventDAO.findById(id);
    }

    @Override
    @Transactional
    public void save(Event event) {
        eventDAO.save(event);
    }

    @Override
    @Transactional
    public void delete(String id) {
        eventDAO.delete(id);
    }

    @Override
    @Transactional
    public void update(Event event) {
        eventDAO.update(event);
    }
}
