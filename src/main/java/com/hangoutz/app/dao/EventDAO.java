package com.hangoutz.app.dao;

import com.hangoutz.app.model.Event;

import java.util.List;

public interface EventDAO {

    List<Event> findAll();

    Event findById(String id);

    Event save(Event event);

    void delete(Event event);

    Event update(Event event);
}
