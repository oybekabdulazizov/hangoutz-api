package com.hangoutz.app.controller;

import com.hangoutz.app.model.Event;
import com.hangoutz.app.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<Collection<Event>> findAll() {
        List<Event> events = eventService.findAll();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<Event> findById(@PathVariable String eventId) {
        Event event = eventService.findById(eventId);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @PostMapping("/events")
    public ResponseEntity<Event> create(@RequestBody Event newEvent) {
        eventService.save(newEvent);
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<Event> update(@PathVariable String eventId, @RequestBody Map<Object, Object> fields) {
        Event event = eventService.findById(eventId);
        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Event.class, (String) key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, event, value);
            }
        });
        eventService.update(event);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<String> delete(@PathVariable String eventId) {
        Event event = eventService.findById(eventId);
        eventService.delete(event);
        return new ResponseEntity<>("Deleted the event with id " + eventId, HttpStatus.OK);
    }
}
