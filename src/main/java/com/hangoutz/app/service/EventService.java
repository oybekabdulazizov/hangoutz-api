package com.hangoutz.app.service;

import com.hangoutz.app.dto.NewEventDTO;
import com.hangoutz.app.model.Event;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Map;

public interface EventService {

    List<Event> findAll();

    Event findById(String id);

    Event create(String bearerToken, NewEventDTO newEventDTO) throws BadRequestException;

    void delete(String bearerToken, String id);

    Event update(String bearerToken, String id, Map<Object, Object> updatedFields) throws BadRequestException;

    Event attend(String bearerToken, String id) throws BadRequestException;

    Event cancel(String bearerToken, String id);
}
