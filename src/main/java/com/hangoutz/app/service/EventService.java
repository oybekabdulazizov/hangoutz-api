package com.hangoutz.app.service;

import com.hangoutz.app.dto.EventDTO;
import com.hangoutz.app.dto.NewEventDTO;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Map;

public interface EventService {

    List<EventDTO> findAll();

    EventDTO findById(String id);

    EventDTO create(String bearerToken, NewEventDTO newEventDTO) throws BadRequestException;

    void delete(String bearerToken, String id);

    EventDTO update(String bearerToken, String id, Map<Object, Object> updatedFields) throws BadRequestException;

    EventDTO attend(String bearerToken, String id) throws BadRequestException;

    EventDTO cancel(String bearerToken, String id);
}
