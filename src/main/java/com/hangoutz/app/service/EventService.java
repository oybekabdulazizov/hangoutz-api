package com.hangoutz.app.service;

import com.hangoutz.app.model.Event;

import java.util.List;
import java.util.Map;

public interface EventService {

    List<Event> findAll();

    Event findById(String id);

    Event save(String jwt, Event event);

    void delete(String id);

    Event update(String id, Map<Object, Object> updatedFields);
}
