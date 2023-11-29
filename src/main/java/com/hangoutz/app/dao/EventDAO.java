package com.hangoutz.app.dao;

import com.hangoutz.app.model.Event;

import java.util.List;

public interface EventDAO {

    List<Event> findAll();

    Event findById(String id);

    void save(Event event);

    void delete(String id);

    void update(Event event);
}
