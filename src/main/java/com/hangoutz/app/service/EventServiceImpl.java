package com.hangoutz.app.service;

import com.hangoutz.app.dao.EventDAO;
import com.hangoutz.app.model.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    private final EventDAO eventDAO;

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
    public void delete(Event event) {
        eventDAO.delete(event);
    }

    @Override
    @Transactional
    public void update(Event event) {
        eventDAO.update(event);
    }
}
