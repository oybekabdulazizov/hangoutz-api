package com.hangoutz.app.controller;

import com.hangoutz.app.dto.EventDTO;
import com.hangoutz.app.mappers.EventMapper;
import com.hangoutz.app.model.Event;
import com.hangoutz.app.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;

    private final EventMapper eventMapper;

    @Autowired
    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @GetMapping("/events")
    public ResponseEntity<Collection<EventDTO>> findAll() {
        List<Event> events = eventService.findAll();
        if (events.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }

        List<EventDTO> eventsDTO = new ArrayList<>();
        for (Event e : events) {
            eventsDTO.add(eventMapper.modelToDto(e));
        }
        return new ResponseEntity<>(eventsDTO, HttpStatus.OK);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventDTO> findById(@PathVariable String eventId) {
        Event event = eventService.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(eventMapper.modelToDto(event), HttpStatus.OK);
    }

    @PostMapping("/events")
    public ResponseEntity<EventDTO> create(@RequestBody EventDTO newEventDTO) {
        Event newEvent = eventMapper.dtoToModel(newEventDTO);
        eventService.save(newEvent);
        return new ResponseEntity<>(eventMapper.modelToDto(newEvent), HttpStatus.CREATED);
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<EventDTO> update(@PathVariable String eventId, @RequestBody Map<Object, Object> fields) {
        Event event = eventService.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        EventDTO eventDTO = eventMapper.modelToDto(event);

        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(EventDTO.class, (String) key);
            if (field != null && !key.equals("id")) {
                field.setAccessible(true);
                if (value == null || value.toString().isBlank()) {
                    throw new IllegalArgumentException(key + " is required");
                }
                ReflectionUtils.setField(field, eventDTO, value);
            }
        });
        eventService.update(eventMapper.dtoToModel(eventDTO));
        return new ResponseEntity<>(eventMapper.modelToDto(event), HttpStatus.OK);
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<String> delete(@PathVariable String eventId) {
        Event event = eventService.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        eventService.delete(event);
        return new ResponseEntity<>("Deleted the event with id " + eventId, HttpStatus.OK);
    }
}
