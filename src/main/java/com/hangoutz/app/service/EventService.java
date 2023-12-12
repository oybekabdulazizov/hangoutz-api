package com.hangoutz.app.service;

import com.hangoutz.app.model.Event;

import java.util.List;
import java.util.Map;

public interface EventService {

    List<Event> findAll();

    Event findById(String id);

    Event save(String bearerToken, Event event);

    void delete(String bearerToken, String id);

    Event update(String bearerToken, String id, Map<Object, Object> updatedFields);
}
