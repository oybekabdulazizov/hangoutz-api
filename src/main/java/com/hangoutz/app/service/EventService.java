package com.hangoutz.app.service;

import com.hangoutz.app.dto.EventDTO;
import com.hangoutz.app.dto.NewEventDTO;
import com.hangoutz.app.dto.UpdateEventDTO;

import java.util.List;

public interface EventService {

    List<EventDTO> findAll();

    EventDTO findById(String id);

    EventDTO create(String bearerToken, NewEventDTO newEventDTO);

    void delete(String bearerToken, String id);

    EventDTO update(String bearerToken, String id, UpdateEventDTO updatedFields);

    EventDTO attend(String bearerToken, String id);

    EventDTO cancel(String bearerToken, String id);
}
